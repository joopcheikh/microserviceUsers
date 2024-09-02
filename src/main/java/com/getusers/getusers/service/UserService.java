package com.getusers.getusers.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.getusers.getusers.dto.UserDTO;
import com.getusers.getusers.model.User;
import com.getusers.getusers.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getRole().equals("ADMIN"))
                .collect(Collectors.toList());
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail(), user.getName()))
                .collect(Collectors.toList());
    }
}
