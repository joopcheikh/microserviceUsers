package com.getusers.getusers.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.getusers.getusers.model.User;
import com.getusers.getusers.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

