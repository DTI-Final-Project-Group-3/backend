package com.warehub.warehub.usecase.security.impl;

import com.warehub.warehub.entity.Role;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.security.RoleCheckUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleCheckUseCaseImpl implements RoleCheckUsecase {
    @Autowired
    private UsersRepository usersRepository;

    public void enforceAdminSuper() {
        Long userId;
        Role currentRole;
        try {
            userId = Claims.getUserIdFromJwt();
            currentRole = usersRepository.findById(userId).get().getRole();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Bearer token not found, please use super admin token to perform this. Or use /api/v1/signup/dev?role=");
        }
        if (!currentRole.getName().equals(RoleType.ADMIN_SUPER.toString())) {
            System.out.println(currentRole.getName());
            throw new RuntimeException("Bearer token valid, but only super admin role can perform this. Or use /api/v1/signup/dev?role=");
        }
    }
}
