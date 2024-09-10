package com.getusers.getusers.dto;


public class UserDTO {
    private Integer id;
    private String email;
    private String role;
    private String lastname;
    private String firstname;
    private String type_candidat;

    public UserDTO(Integer integer, String email, String string, String lastname, String firstname, String type_candidat) {
        this.id = integer;
        this.email = email;
        this.role = string;
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

    public String getRole() {
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
