package com.getusers.getusers.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;

    /** Relation avec les utilisateurs */
    @OneToMany(mappedBy = "region")
    private List<User> users;
}
