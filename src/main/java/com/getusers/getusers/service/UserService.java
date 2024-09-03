package com.getusers.getusers.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.getusers.getusers.dto.UserDTO;
import com.getusers.getusers.model.User;
import com.getusers.getusers.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        addUser("user1@example.com", "User One", "password1", "USER", "user1");
        addUser("user2@example.com", "User Two", "password2", "USER","user2");
        addUser("admin@example.com", "Admin User", "adminpassword", "ADMIN","admin");
    }

    public void addUser(String email, String name, String rawPassword, String role, String username) {
        // Encode the password
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Create a new user object
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setUsername(username);

        // Save the user to the database
        userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getRole().equals("ADMIN"))
                .collect(Collectors.toList());
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail(), user.getName()))
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
