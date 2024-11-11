package com.getusers.getusers.model;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * @author cheikh diop
 * Représente un utilisateur du système.
 * Cette classe implémente l'interface UserDetails de Spring Security,
 * permettant de gérer les détails de l'utilisateur lors de l'authentification.
 */
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /** identifiant unique de l'utilisateur */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** prenom de l'utilisateur */
    private String firstname;

    /** nom de l'utilisateur */
    private String lastname;

    /** Adresse email unique de l'utilisateur. */
    @Column(unique = true)
    private String email;

    /** Mot de passe de l'utilisateur. */
    private String password;

    /** numero de l'utilisateur. */
    private String phoneNumber;

    private Boolean haspaid = false;

    /** Role de l'utilisateur (ex: USER, ADMIN, etc.) */
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    /** Genre de l'utilisateur (ex: Masculin, Feminin ou autres.) */
    @Enumerated(value = EnumType.STRING)
    private Genre genre;

    /** Relation avec la région */
    @ManyToOne
    @JoinColumn(name = "region_id")
    @JsonIgnore
    private Region region;

    @ManyToOne
    @JoinColumn(name = "departement_id")
    @JsonIgnore
    private Departement departement;

    @ManyToOne
    @JoinColumn(name = "commune_id")
    @JsonIgnore
    private Commune commune;

    private String ethnies;

    /**
     * Retourne les autorités accordées à l'utilisateur.
     * @return une collection d'autorités.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role.name());
    }

    /**
     * Retourne le password de l'utilisateur.
     *
     * @return password.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Retourne le username de l'utilisateur qui est l'email dans ce projet.
     *
     * @return email.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indique si l'utilisateur est actif.
     *
     * @return true si l'utilisateur est actif, sinon false.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indique si le compte de l'utilisateur est verrouillé.
     *
     * @return true si le compte est non verrouillé, sinon false.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indique si les informations d'identification de l'utilisateur ont expiré.
     *
     * @return true si les informations d'identification sont valides, sinon false.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indique si l'utilisateur est toujours actif.
     *
     * @return true si l'utilisateur est actif, sinon false.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
