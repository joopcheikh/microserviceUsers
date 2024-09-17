package com.getusers.getusers.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.getusers.getusers.dto.UserDTO;
import com.getusers.getusers.model.User;
import com.getusers.getusers.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHistoryService userHistoryService; // Injection du service d'historique

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserHistoryService userHistoryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userHistoryService = userHistoryService;
    }

    
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail(), user.getRole(), // Remplacez user.getRole() par null
                        user.getFirstname(), user.getLastname(), user.getType_candidat()))
                .collect(Collectors.toList());
    }
    

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User addUser(User user) {
        User existingUser = userRepository.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("duplicate");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);

        // Enregistrer l'action dans l'historique
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminName = authentication.getName();  // Admin connecté
        userHistoryService.saveHistory("ADD", savedUser, null, adminName);

        return savedUser;
    }

    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());
        if (existingUser == null) {
            return null;
        }

        User updatedUser = userRepository.save(user);

        // Enregistrer l'action dans l'historique
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminName = authentication.getName();
        userHistoryService.saveHistory("UPDATE", updatedUser, existingUser, adminName);

        return updatedUser;
    }

    public void deleteUserById(Integer userId) {
        User user = getUserById(userId);
        if (user != null) {
            userRepository.deleteById(userId);

            // Enregistrer l'action dans l'historique
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminName = authentication.getName();
            userHistoryService.saveHistory("DELETE", user, null, adminName);
        }
    }

    public String deleteUser(User user) {
        userRepository.delete(user);

        // Enregistrer l'action dans l'historique
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminName = authentication.getName();
        userHistoryService.saveHistory("DELETE", user, null, adminName);

        return "User deleted successfully";
    }
}
