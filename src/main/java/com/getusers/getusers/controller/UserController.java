package com.getusers.getusers.controller;

import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.getusers.getusers.dto.UserDTO;
import com.getusers.getusers.model.User;
import com.getusers.getusers.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/users")
    public Map<String, Object> getUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = authentication.getAuthorities().iterator().next().getAuthority();
        System.out.println(userRole);
        Map<String, Object> response = new HashMap<>();

        if ("ADMIN".equals(userRole)) {
            List<UserDTO> users = userService.getAllUsers();
            response.put("users", users);
            response.put("role", "ADMIN");
        } else if ("RECRUITER".equals(userRole)) {
            response.put("users", Collections.emptyList());
            response.put("role", "RECRUITER");
        } else {
            response.put("users", Collections.emptyList());
            response.put("role", "UNKNOWN");
        }

        return response;
    }

    @PostMapping("/add/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {

        User createdUser = userService.addUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PatchMapping("/update/user/{userId}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable("userId") Integer userId) {
        User existingUser = userService.getUserById(userId);

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        existingUser.setEmail(user.getEmail());
        existingUser.setType_candidat(user.getType_candidat());
        existingUser.setRole(user.getRole());
     
        User updatedUser = userService.updateUser(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Integer userId) {
        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

}
