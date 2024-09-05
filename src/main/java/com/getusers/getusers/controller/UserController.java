package com.getusers.getusers.controller;

import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.getusers.getusers.dto.UserDTO;
import com.getusers.getusers.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
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
}
