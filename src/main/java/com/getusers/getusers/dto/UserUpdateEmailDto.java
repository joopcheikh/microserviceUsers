
package com.getusers.getusers.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateEmailDto {

    @NotNull(message = "L'ancien email ne peut pas être nul")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Format de l'ancien email invalide")
    private String oldEmail;

    @NotNull(message = "Le nouvel email ne peut pas être nul")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Format du nouvel email invalide")
    private String newEmail;

    @NotNull(message = "Le mot de passe ne peut pas être nul")
    private String password;
}
