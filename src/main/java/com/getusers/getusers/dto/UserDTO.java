package com.getusers.getusers.dto;

import com.getusers.getusers.model.Role;

public class UserDTO {
    private Integer id;
    private String email;
    private Role role;
    private String lastname;
    private String firstname;
    private String type_candidat;

    public UserDTO(Integer id, String email, Role role, String lastname, String firstname, String type_candidat) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.firstname = firstname;
        this.lastname = lastname;
        this.type_candidat = type_candidat;
    }

    public Integer getId() {
        return id;
    }

    public String getType_candidat() {
        return type_candidat;
    }
     
    public Role getRole() {
        return role;
    } 

    public String getEmail() {
        return email;
    }


    public String getFirstName() {
        return firstname;
    }

    public String getLastName() {
        return lastname;
    }
}
