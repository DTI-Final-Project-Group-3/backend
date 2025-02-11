package com.warehub.warehub.infrastructure.users.dto;

import com.warehub.warehub.entity.User;
import lombok.Data;

@Data
public class CreateUserRequestDTO {
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
