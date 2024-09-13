package com.getusers.getusers.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.getusers.getusers.dto.UserDTO;
import com.getusers.getusers.model.User;
import com.getusers.getusers.repository.UserRepository;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll().stream()
                .collect(Collectors.toList());
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
        return userRepository.save(user);
    }

    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public String deleteUser(User user) {
        return null;
    }
}
