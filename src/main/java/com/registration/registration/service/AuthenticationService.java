package com.registration.registration.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.registration.registration.model.User;
import com.registration.registration.repository.UserRepository;



@Service
public class AuthenticationService {

    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }



    // est une méthode utilisée pour enregistrer un nouvel utilisateur dans un système d'authentification.
    public AuthenticationResponse register(User userRigistry) {
        // Ici, un nouvel objet User est créé pour représenter l'utilisateur à enregistrer.
        User user = new User();
        /* Les informations fournies dans userRegister (qui est l'utilisateur à enregistrer)
         * sont copiées dans l'objet user.
         */
        user.setFirstname(userRigistry.getFirstname());
        user.setLastname(userRigistry.getLastname());
        user.setUsername(userRigistry.getUsername());
        user.setPassword(passwordEncoder.encode(userRigistry.getPassword()));
        user.setRole(userRigistry.getRole());

        User savedUser = userRepository.save(user);
        // générer un token pour cet utilisateur
        String token = jwtService.generateToken(savedUser);

        /*
         * la méthode retourne une nouvelle instance de AuthenticationResponse, qui encapsule
         * le jeton JWT généré. Cela permet au client de récupérer et d'utiliser ce jeton pour
         * s'authentifier dans les requêtes futures.
         */
        return new AuthenticationResponse(token);
    }



    // méthode utilisée pour authentifier un utilisateur
    public AuthenticationResponse login(User authUser){
        /* authUser: l'objet qui contient les informations
         * d'authentification(nom d'utilisateur et mot de passe)
         *
         * authenticationManager.authenticate(...) : cette méthode tente
         * d'authentifier un utilisateur en utilisant les infos fournies
         */

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authUser.getUsername(),
                        authUser.getPassword()
                )
        );

        // on va essayer de récupérer le user dans la base de données
        User user = userRepository.findUserByUsername(authUser.getUsername()).orElseThrow();
        // si le user est touver, on lui génére un token
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }


    // Methode pour les candidatures
    public String candidature(String firstname, String lastname, String username, MultipartFile file) throws IOException {
        // Vérifiez que le fichier est un PDF
        if (file == null || !file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Le fichier doit être un PDF.");
        }
    
        // Vérifiez si l'utilisateur existe déjà
        Optional<User> existingUser = userRepository.findUserByUsername(username);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec ce nom d'utilisateur existe déjà.");
        }
    
        // Enregistrez le fichier PDF
        Path directory = Paths.get("candidatures/");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    
        //S'assurer que le nom du fichier est sûr et valide
        String filename = file.getOriginalFilename();
        if (filename == null || filename.contains("..")) {
            throw new IllegalArgumentException("Nom de fichier invalide.");
        }
    
        Path path = directory.resolve(filename);
    
        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new IOException("Erreur lors de l'enregistrement du fichier.", e);
        }
    
        // Enregistrez les informations de l'utilisateur
        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setUsername(username);
        // user.setRole("ROLE_CANDIDATE"); // Décommentez si nécessaire
        // user.setPassword(""); // Définissez un mot de passe si nécessaire
    
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de l'utilisateur.", e);
        }
    
        return "Candidature reçue avec succès.";
    }
    
}
