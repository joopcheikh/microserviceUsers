package com.getusers.getusers.service;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.getusers.getusers.dto.UserDTO;
import com.getusers.getusers.dto.UserUpdateNameDto;
import com.getusers.getusers.dto.UserUpdatePasswordDto;
import com.getusers.getusers.model.User;
import com.getusers.getusers.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHistoryService userHistoryService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            UserHistoryService userHistoryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userHistoryService = userHistoryService;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail(), user.getRole(),
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
        String adminName = getAdminName(authentication);
        String adminEmail = getAdminEmail(authentication);
        userHistoryService.saveHistory("ADD", savedUser, null, adminName, adminEmail);

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
        String adminName = getAdminName(authentication);
        String adminEmail = getAdminEmail(authentication);
        userHistoryService.saveHistory("UPDATE", updatedUser, existingUser, adminName, adminEmail);

        return updatedUser;
    }

    public void deleteUserById(Integer userId) {
        User user = getUserById(userId);
        if (user != null) {
            userRepository.deleteById(userId);

            // Enregistrer l'action dans l'historique
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminName = getAdminName(authentication);
            String adminEmail = getAdminEmail(authentication);
            userHistoryService.saveHistory("DELETE", user, null, adminName, adminEmail);
        }
    }

    public String deleteUser(User user) {
        userRepository.delete(user);

        // Enregistrer l'action dans l'historique
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminName = getAdminName(authentication);
        String adminEmail = getAdminEmail(authentication);
        userHistoryService.saveHistory("DELETE", user, null, adminName, adminEmail);

        return "User deleted successfully";
    }

    private String getAdminName(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User admin = userRepository.findUserByEmail(email);
            return admin != null ? admin.getFirstname() : "Unknown";
        }
        return "Unknown";
    }

    private String getAdminEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return null;
    }

    public boolean updateUserPassword(String email, UserUpdatePasswordDto userUpdatePasswordDto) {
        Optional<User> userOptional = Optional.of(userRepository.findUserByEmail(email));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!passwordEncoder.matches(userUpdatePasswordDto.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Ancien mot de passe incorrect");
            }

            user.setPassword(passwordEncoder.encode(userUpdatePasswordDto.getNewPassword()));
            userRepository.save(user);
            return true;
        } else {
            throw new EntityNotFoundException("Utilisateur non trouvé");
        }
    }

    public User updateUserNameByEmail(String email, UserUpdateNameDto userUpdateNameDto) {
        Optional<User> userOptional = Optional.of(userRepository.findUserByEmail(email));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFirstname(userUpdateNameDto.getFirstname());
            user.setLastname(userUpdateNameDto.getLastname());
            return userRepository.save(user);
        } else {
            throw new EntityNotFoundException("Utilisateur non trouvé avec cet email");
        }
    }

}
