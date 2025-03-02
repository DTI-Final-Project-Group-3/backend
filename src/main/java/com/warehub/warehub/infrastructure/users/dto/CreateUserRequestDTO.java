package com.warehub.warehub.infrastructure.users.dto;

import com.warehub.warehub.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequestDTO {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    private String password;
    private String fullname;

    public User toEntity() {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(password);
        if (fullname != null && !fullname.trim().isEmpty()) {
            user.setFullname(fullname);
        }
        return user;
    }
}
