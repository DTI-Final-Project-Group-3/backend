package com.warehub.warehub.infrastructure.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailRequestDTO implements Serializable {
    private Long id;
    private String username;
    private String email;
    private Boolean isEmailVerified;
    private String passwordHash;
    private String role;
    private String fullname;
    private String gender;
    private String biodata;
    private LocalDate birthdate;
    private String phoneNumber;
    private String profileImageUrl;
    private OffsetDateTime createdAt;
}
