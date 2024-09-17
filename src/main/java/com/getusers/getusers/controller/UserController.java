package com.getusers.getusers.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.getusers.getusers.dto.UserDTO;
import com.getusers.getusers.model.User;
import com.getusers.getusers.service.UserService;
import com.getusers.getusers.service.UserHistoryService;

@RestController
public class UserController {

    private final UserService userService;
    private final UserHistoryService userHistoryService; // Injection du service d'historique

    public UserController(UserService userService, UserHistoryService userHistoryService) {
        this.userService = userService;
        this.userHistoryService = userHistoryService;
    }

    @GetMapping("/users")
    public Map<String, Object> getUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = authentication.getAuthorities().iterator().next().getAuthority();
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
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.addUser(user);

            // Enregistrer l'action dans l'historique
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminName = authentication.getName();
            userHistoryService.saveHistory("ADD", createdUser, null, adminName);

            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

        // Enregistrer l'action dans l'historique
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminName = authentication.getName();
        userHistoryService.saveHistory("UPDATE", updatedUser, existingUser, adminName);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Integer userId) {
        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUserById(userId);

        // Enregistrer l'action dans l'historique
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminName = authentication.getName();
        userHistoryService.saveHistory("DELETE", existingUser, null, adminName);

        return ResponseEntity.noContent().build();
    }
}
