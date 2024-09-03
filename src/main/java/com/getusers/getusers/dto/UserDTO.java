package com.getusers.getusers.dto;

public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String role;

    public UserDTO(Long id, String email, String name, String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getRole(){
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
