package com.getusers.getusers.dto;

import com.getusers.getusers.model.Commune;
import com.getusers.getusers.model.Departement;
import com.getusers.getusers.model.Genre;
import com.getusers.getusers.model.Region;
import com.getusers.getusers.model.Role;

import lombok.Data;

@Data
public class UserDTO {
    public UserDTO(Integer id2, String firstname2, String lastname2, String email2, String phoneNumber2,
                   Boolean haspaid2, Role role2, Genre genre2, Region region2, Departement departement2, Commune commune2,
                   String ethnies2) {
        this.id = id2;
        this.firstname = firstname2;
        this.lastname = lastname2;
        this.email = email2;
        this.phoneNumber = phoneNumber2;
        this.haspaid = haspaid2;
        this.role = role2;
        this.genre = genre2;
        this.region = region2;
        this.departement = departement2;
        this.commune = commune2;
        this.ethnies = ethnies2;
    }

    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private Boolean haspaid;
    private Role role;
    private Genre genre;
    private Region region;
    private Departement departement;
    private Commune commune;
    private String ethnies;
}
