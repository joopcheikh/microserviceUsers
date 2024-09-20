package com.getusers.getusers.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_histories")
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String actionType; // ADD, UPDATE, DELETE
    private String email;
    private String firstname;
    private String lastname;
    private String type_candidat;
    private String adminName;
    private String adminEmail;
    private Role role;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();
}
