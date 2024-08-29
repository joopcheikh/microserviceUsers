package com.registration.registration.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // Methode pour les candidatures
    public String candidature(String username, String firstname, String telephone, String sexe, String address, Date birthdate, String birthplace, String cnicardnumber, String filePath) throws IOException {
        // Vérifiez que le fichier est un PDF
        Path file = Paths.get(filePath);
        if (!Files.exists(file) || !Files.probeContentType(file).equals("application/pdf")) {
            throw new IllegalArgumentException("Le fichier doit être un PDF.");
        }

        // Vérifiez si l'utilisateur existe déjà
        Optional<User> existingUser = userRepository.findUserByUsername(username);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec ce nom d'utilisateur existe déjà.");
        }

        // Enregistrez les informations de l'utilisateur
        User user = new User();
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setTelephone(telephone);
        user.setSexe(sexe);
        user.setAdress(address);
        user.setBirthdate(birthdate);
        user.setBirthplace(birthplace);
        user.setCnicardnumber(cnicardnumber);
        user.setFilePath(filePath); // Stocker le chemin du fichier
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
