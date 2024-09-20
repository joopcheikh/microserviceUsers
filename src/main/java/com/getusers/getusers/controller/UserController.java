package com.getusers.getusers.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.getusers.getusers.dto.UserDTO;
import com.getusers.getusers.dto.UserUpdateEmailDto;
import com.getusers.getusers.dto.UserUpdateNameDto;
import com.getusers.getusers.dto.UserUpdatePasswordDto;
import com.getusers.getusers.model.User;
import com.getusers.getusers.model.UserHistory;
import com.getusers.getusers.repository.UserRepository;
import com.getusers.getusers.service.JwtService;
import com.getusers.getusers.service.UserHistoryService;
import com.getusers.getusers.service.UserService;

@RestController
public class UserController {

    private final UserService userService;
    private final UserHistoryService userHistoryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserController(UserService userService, UserHistoryService userHistoryService, UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtService JwtService) {

        this.userService = userService;
        this.jwtService = JwtService;
        this.userHistoryService = userHistoryService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    @GetMapping("/get-histories")
    public ResponseEntity<List<UserHistory>> getHistories() {
        List<UserHistory> histories = userHistoryService.getAllHistories();
        return ResponseEntity.ok(histories);
    }

    @PostMapping("/add/user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.addUser(user);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminName = getAdminName(authentication);
            String adminEmail = getAdminEmail(authentication);
            userHistoryService.saveHistory("ADD", createdUser, null, adminName, adminEmail);

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
        String adminName = getAdminName(authentication);
        String adminEmail = getAdminEmail(authentication);
        userHistoryService.saveHistory("UPDATE", updatedUser, existingUser, adminName, adminEmail);

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
        String adminName = getAdminName(authentication);
        String adminEmail = getAdminEmail(authentication);
        userHistoryService.saveHistory("DELETE", existingUser, null, adminName, adminEmail);

        return ResponseEntity.noContent().build();
    }

    private String getAdminName(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User admin = userService.getUserByEmail(email);
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

    @PutMapping("/update/personnal/name")
    public ResponseEntity<Map<String, String>> updateUserName(@RequestBody UserUpdateNameDto userUpdateNameDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            userEmail = userDetails.getUsername();
        }

        User currentUser = userRepository.findUserByEmail(userEmail);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Utilisateur non trouvé."));
        }

        currentUser.setFirstname(userUpdateNameDto.getFirstname());
        currentUser.setLastname(userUpdateNameDto.getLastname());
        userRepository.save(currentUser);

        String newToken = jwtService.generateToken(currentUser);
        Map<String, String> response = new HashMap<>();
        response.put("token", newToken);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/personnal/password")
    public ResponseEntity<Map<String, String>> updateUserPassword(
            @RequestBody UserUpdatePasswordDto userUpdatePasswordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            userEmail = userDetails.getUsername();
        }
        User user = userRepository.findUserByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Utilisateur non trouvé"));
        }
        boolean isUpdated = userService.updateUserPassword(userEmail, userUpdatePasswordDto);
        if (isUpdated) {
            String newToken = jwtService.generateToken(user);
            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Erreur lors de la mise à jour du mot de passe"));
        }
    }

    @PutMapping("/update/personnal/email")
    public ResponseEntity<Map<String, String>> updateUserEmail(@RequestBody UserUpdateEmailDto userUpdateEmailDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;
    
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            userEmail = userDetails.getUsername();
        }
    
        User currentUser = userRepository.findUserByEmail(userEmail);
    
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Utilisateur non trouvé."));
        }
    
        if (!currentUser.getEmail().equals(userUpdateEmailDto.getOldEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "L'ancien email ne correspond pas."));
        }
    
        if (!passwordEncoder.matches(userUpdateEmailDto.getPassword(), currentUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Mot de passe incorrect."));
        }
    
        if (!currentUser.getEmail().equals(userUpdateEmailDto.getNewEmail()) ) {
            currentUser.setEmail(userUpdateEmailDto.getNewEmail());
            userRepository.save(currentUser);
            
            String newToken = jwtService.generateToken(currentUser);
            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Le nouvel email ne peut pas être identique à l'ancien."));
        }
    }
    

}
