package com.warehub.warehub.usecase.admin.impl;

import com.warehub.warehub.entity.*;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.admin.dto.AssignWarehouseRequestDTO;
import com.warehub.warehub.infrastructure.admin.dto.AssignWarehouseResponseDTO;
import com.warehub.warehub.infrastructure.admin.dto.UserAdminDetailResponseDTO;
import com.warehub.warehub.infrastructure.admin.dto.UserAdminUpdateRequestDTO;
import com.warehub.warehub.infrastructure.login.dto.UserAuth;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.signup.repository.EmailVerificationTokenRepository;
import com.warehub.warehub.infrastructure.users.dto.*;
import com.warehub.warehub.infrastructure.users.repository.RolesRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseAdminRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.security.RoleCheckUsecase;
import com.warehub.warehub.usecase.admin.AdminUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminUsecaseImpl implements AdminUsecase {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private WarehouseAdminRepository warehouseAdminRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private RoleCheckUsecase roleCheckUsecase;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    public List<UserAdminDetailResponseDTO> getAllAdminWarehouseBase(boolean selectNotAssigned) {
        roleCheckUsecase.enforceAdminSuper();
        Role role = rolesRepository.findByName(RoleType.ADMIN_WAREHOUSE.toString()).get();
        return warehouseAdminRepository.findAllWarehouseAdmins(role.getId(), selectNotAssigned);
    }

    @Override
    public List<UserAdminDetailResponseDTO> getAllAdminWarehouse() {
        roleCheckUsecase.enforceAdminSuper();
        return getAllAdminWarehouseBase(false);
    }

    @Override
    public List<UserAdminDetailResponseDTO> getAllAdminWarehouseNotAssigned() {
        roleCheckUsecase.enforceAdminSuper();
        return getAllAdminWarehouseBase(true);
    }

    @Override
    public List<UserAdminDetailResponseDTO> getAllAdminWarehouseAssigned(Long warehouseId) {
        roleCheckUsecase.enforceAdminSuper();
        return warehouseAdminRepository.findAdminsByWarehouseId(warehouseId);
    }

    @Override
    public AssignWarehouseResponseDTO assignWarehouse(AssignWarehouseRequestDTO request) {
        User superAdmin = UserAuth.getCurrentUser(usersRepository);
        roleCheckUsecase.enforceAdminSuper();

        User assignee = usersRepository.findById(request.getUserAssigneeId()).get();
        if (!assignee.getRole().getName().equals(RoleType.ADMIN_WAREHOUSE.toString())) {
            throw new RuntimeException("Input user is not admin warehouse role");
        }
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(request.getWarehouseId()).get();

        Optional<WarehouseAdmin> existingWarehouseAdmin = warehouseAdminRepository.findByUserAssigneeId(assignee.getId());
        if (existingWarehouseAdmin.isPresent()) {
            throw new RuntimeException("This user is already assigned a warehouse");
        }

        WarehouseAdmin warehouseAdmin = new WarehouseAdmin();
        warehouseAdmin.setWarehouse(warehouse);
        warehouseAdmin.setUserAssignee(assignee);
        warehouseAdmin.setUserAssigner(superAdmin);
        WarehouseAdmin savedWarehouseAdmin = warehouseAdminRepository.save(warehouseAdmin);

        AssignWarehouseResponseDTO response = new AssignWarehouseResponseDTO();
        response.setId(savedWarehouseAdmin.getId());
        response.setWarehouseId(savedWarehouseAdmin.getWarehouse().getId());
        response.setUserAssigneeId(savedWarehouseAdmin.getUserAssignee().getId());
        response.setUserAssignerId(savedWarehouseAdmin.getUserAssigner().getId());

        return response;
    }

    @Override
    public AssignWarehouseResponseDTO removeWarehouseAssignment(AssignWarehouseRequestDTO request) {
        User superAdmin = UserAuth.getCurrentUser(usersRepository);
        roleCheckUsecase.enforceAdminSuper();

        AssignWarehouseResponseDTO responseDTO = new AssignWarehouseResponseDTO();



        User assignee = usersRepository.findById(request.getUserAssigneeId()).get();
        if (!assignee.getRole().getName().equals(RoleType.ADMIN_WAREHOUSE.toString())) {
            throw new RuntimeException("Input user is not admin warehouse role");
        }

        Optional<WarehouseAdmin> existingWarehouseAdmin = warehouseAdminRepository.findByUserAssigneeId(assignee.getId());
        if (existingWarehouseAdmin.isEmpty()) {
            throw new RuntimeException("This user is not assigned a warehouse");
        }

        responseDTO.setId(existingWarehouseAdmin.get().getId());
        responseDTO.setWarehouseId(existingWarehouseAdmin.get().getWarehouse().getId());
        responseDTO.setUserAssigneeId(assignee.getId());
        responseDTO.setUserAssignerId(superAdmin.getId());

        warehouseAdminRepository.delete(existingWarehouseAdmin.get());

        return responseDTO;
    }

    @Override
    public CurrentWarehouseResponseDTO getCurrentWarehouseDTO() {
        roleCheckUsecase.enforceAdminSuper();

        CurrentWarehouseResponseDTO responseDTO = new CurrentWarehouseResponseDTO();
        Long userId = Claims.getUserIdFromJwt();
        Optional<WarehouseAdmin> warehouseAdmin = warehouseAdminRepository.findByUserAssigneeId(userId);
        if (warehouseAdmin.isPresent()) {
            responseDTO.setWarehouseId(warehouseAdmin.get().getWarehouse().getId());
            responseDTO.setUserAssignerId(warehouseAdmin.get().getUserAssigner().getId());
        } else {
            responseDTO.setUserAssignerId(-1L);
            responseDTO.setWarehouseId(-1L);
        }
        return responseDTO;
    }

    @Override
    public String deleteAdmin(Long userId) {
        roleCheckUsecase.enforceAdminSuper();

        User user = usersRepository.findById(userId).get();
        usersRepository.delete(user);

        return "Success";
    }

    @Override
    public List<UserDetailResponseDTO> getAllCustomerVerified() {
        roleCheckUsecase.enforceAdminSuper();

        Role role = rolesRepository.findByName(RoleType.CUSTOMER_VERIFIED.toString()).get();
        List<User> users = usersRepository.findByRoleIdAndDeletedAtIsNull(role.getId());

        return users.stream()
                .map(user -> new UserDetailResponseDTO().copyFromUser(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDetailResponseDTO updateAdminDetail(Long id, UserAdminUpdateRequestDTO req) {
        roleCheckUsecase.enforceAdminSuper();

        User user = usersRepository.findById(id).get();
        if (req.getFullname() != null)
            user.setFullname(req.getFullname());

        if (req.getProfileImageUrl() != null)
            user.setProfileImageUrl(req.getProfileImageUrl());

        if (req.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }

        User savedUser = usersRepository.save(user);

        return new UserDetailResponseDTO().copyFromUser(savedUser);
    }

    @Override
    public UserDetailResponseDTO getAdminDetail(Long id) {
        roleCheckUsecase.enforceAdminSuper();
        User user = usersRepository.findById(id).get();
        return new UserDetailResponseDTO().copyFromUser(user);
    }

    @Override
    public String deleteUserByEmail(String email) {
        roleCheckUsecase.enforceAdminSuper();

        Optional<User> user = usersRepository.findByEmailIgnoreCase(email);

        if (user.isEmpty()) {
            throw new RuntimeException("User with email " + email + " not found");
        }

        Optional<EmailVerificationToken> emailVerificationToken = emailVerificationTokenRepository.findByUserId(user.get().getId());
        if (emailVerificationToken.isPresent()) {
            emailVerificationTokenRepository.delete(emailVerificationToken.get());
        }

        usersRepository.delete(user.get());

        return "Success delete user " + email;
    }
}
