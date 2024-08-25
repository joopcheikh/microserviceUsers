package com.registration.registration.service;

import com.registration.registration.model.User;
import com.registration.registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
