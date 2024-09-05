package com.getusers.getusers.dto;

public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String role;
    private String lastname;
    private String firstname;

    public UserDTO(Long id, String email, String name, String role, String lastname, String firstname) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstname;
    }

    public String getLastName() {
        return lastname;
    }
}
