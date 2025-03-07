package com.warehub.warehub.usecase.security.impl;

import com.warehub.warehub.entity.Role;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.users.dto.UserAuth;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.security.RoleCheckUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleCheckUseCaseImpl implements RoleCheckUsecase {
    @Autowired
    private UsersRepository usersRepository;

    public void enforceAdminSuper() {
        Long userId = Claims.getUserIdFromJwt();
        Role currentRole = usersRepository.findById(userId).get().getRole();
        if (!currentRole.getName().equals(RoleType.ADMIN_SUPER.toString())) {
            System.out.println(currentRole.getName());
            throw new RuntimeException("Not a super admin role");
        }
    }
}
