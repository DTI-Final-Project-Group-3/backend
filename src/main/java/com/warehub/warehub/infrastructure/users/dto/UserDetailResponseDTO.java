package com.warehub.warehub.infrastructure.users.dto;

import com.warehub.warehub.entity.User;
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
    private String role;
    private String fullname;
    private String gender;
    private String biodata;
    private LocalDate birthdate;
    private String phoneNumber;
    private String profileImageUrl;
    private OffsetDateTime createdAt;
    private Long warehouseId = -1L;
    private Long userAssignerId = -1L;

    public UserDetailResponseDTO copyFromUser(User user) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        isEmailVerified = user.getIsEmailVerified();
        passwordHash = "";
        role = user.getRole().getName();
        fullname = user.getFullname();
        gender = user.getGender();
        biodata = user.getBiodata();
        birthdate = user.getBirthdate();
        phoneNumber = user.getPhoneNumber();
        profileImageUrl = user.getProfileImageUrl();
        createdAt = user.getCreatedAt();
        return this;
    }
}