package com.getusers.getusers.service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.getusers.getusers.model.User;
import com.getusers.getusers.repository.UserRepository;


@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userRepository;
    public UserDetailsServiceImp(UserRepository repository) {
        this.userRepository = repository;
    }

    /**
     * This method allow us to load user from database by this username
     * 
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        List<GrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(String.valueOf(user.getRole())));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public String deleteUser(User user) {
        userRepository.delete(user);
        return "utilisateur supprime avec succes ";
    }
}
