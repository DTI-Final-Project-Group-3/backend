package com.warehub.warehub.infrastructure.users.dto;

import com.warehub.warehub.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDTO implements Serializable {
    private Long id;
    private String username;
    private String email;
    private Boolean isEmailVerified = false;
    private String passwordHash;
    private Long role_id;
    private String fullname;
    private String gender;
    private String biodata;
    private LocalDate birthdate;
    private String phoneNumber;
    private String profileImageUrl;
}