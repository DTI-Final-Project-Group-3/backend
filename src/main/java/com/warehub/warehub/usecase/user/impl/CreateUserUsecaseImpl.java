package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.users.dto.CreateUserRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.RolesRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.CreateUserUsecase;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.warehub.warehub.entity.enums.RoleUtil.roleEnumFromString;

@Service
public class CreateUserUsecaseImpl implements CreateUserUsecase {
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUsecaseImpl(UsersRepository usersRepository,
                                 RolesRepository rolesRepository,
                                 PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetailResponseDTO createUser(CreateUserRequestDTO req, String role){
        RoleType roleType = roleEnumFromString(role, RoleType.NOT_VERIFIED);
        User newUser = req.toEntity();
        newUser.setPasswordHash(passwordEncoder.encode(newUser.getPasswordHash()));
        newUser.setRole(rolesRepository.findByName(roleType.toString()).get());
        newUser.setIsEmailVerified(roleType != RoleType.NOT_VERIFIED);

        var savedUser = usersRepository.save(newUser);
        return new UserDetailResponseDTO().copyFromUser(savedUser);
    }
}
