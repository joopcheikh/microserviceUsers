package com.registration.registration.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.registration.registration.service.AuthenticationService;

@RestController
public class CandidatureController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/candidature")
    public ResponseEntity<String> candidature(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("username") String username,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String response = authenticationService.candidature(firstname, lastname, username, file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erreur lors du traitement de la candidature.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
