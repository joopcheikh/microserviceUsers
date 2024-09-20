package com.getusers.getusers.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserUpdatePasswordDto {

    @Column(nullable = false)
    private String oldPassword;

    @Column(nullable = false)
    private String newPassword;
}
