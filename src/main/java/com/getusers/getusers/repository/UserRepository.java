package com.getusers.getusers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.getusers.getusers.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @SuppressWarnings("null")
    List<User> findAll();
}
