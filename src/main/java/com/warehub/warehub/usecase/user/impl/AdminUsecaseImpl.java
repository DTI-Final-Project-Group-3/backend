package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.entity.Role;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.entity.WarehouseAdmin;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.users.dto.*;
import com.warehub.warehub.infrastructure.users.repository.RolesRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseAdminRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.user.AdminUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    public List<UserAdminDetailResponseDTO> getAllAdminWarehouseBase(boolean selectNotAssigned) {
        Role currentRole = UserAuth.getCurrentUser(usersRepository).getRole();
        if (!currentRole.getName().equals(RoleType.ADMIN_SUPER.toString())) {
            System.out.println(currentRole.getName());
            throw new RuntimeException("Not a super admin role");
        }

        List<UserAdminDetailResponseDTO> result = new ArrayList<>();
        Role role = rolesRepository.findByName(RoleType.ADMIN_WAREHOUSE.toString()).get();
        List<User> users = usersRepository.findByRoleIdAndDeletedAtIsNull(role.getId());

        for (int loop = 0; loop < users.size(); loop++) {
            UserAdminDetailResponseDTO admin = new UserAdminDetailResponseDTO().copyFromUser(users.get(loop));
            Optional<WarehouseAdmin> warehouseAdmin = warehouseAdminRepository.findByUserAssigneeId(admin.getId());
            if (selectNotAssigned && warehouseAdmin.isPresent()) {
                continue;
            }
            if (!warehouseAdmin.isEmpty()) {
                admin.setWarehouseId(warehouseAdmin.get().getWarehouse().getId());
                admin.setUserAssignerId(warehouseAdmin.get().getUserAssigner().getId());
            }
            result.add(admin);
        }
        return result;
    }

    @Override
    public List<UserAdminDetailResponseDTO> getAllAdminWarehouse() {
        return getAllAdminWarehouseBase(false);
    }

    @Override
    public List<UserAdminDetailResponseDTO> getAllAdminWarehouseNotAssigned() {
        return getAllAdminWarehouseBase(true);
    }

    @Override
    public List<UserAdminDetailResponseDTO> getAllAdminWarehouseAssigned(Long warehouseId) {
        Role currentRole = UserAuth.getCurrentUser(usersRepository).getRole();
        if (!currentRole.getName().equals(RoleType.ADMIN_SUPER.toString())) {
            System.out.println(currentRole.getName());
            throw new RuntimeException("Not a super admin role");
        }
        List<UserAdminDetailResponseDTO> result = new ArrayList<>();
        List<WarehouseAdmin> warehouseAdmins = warehouseAdminRepository.findByWarehouseId(warehouseId);

        for (int loop = 0; loop < warehouseAdmins.size(); loop++) {
            UserAdminDetailResponseDTO admin = new UserAdminDetailResponseDTO().copyFromUser(warehouseAdmins.get(loop).getUserAssignee());
            admin.setWarehouseId(warehouseId);
            admin.setUserAssignerId(warehouseAdmins.get(loop).getUserAssigner().getId());
            result.add(admin);
        }

        return result;
    }

    @Override
    public AssignWarehouseResponseDTO assignWarehouse(AssignWarehouseRequestDTO request) {
        User superAdmin = UserAuth.getCurrentUser(usersRepository);
        Role currentRole = superAdmin.getRole();
        if (!currentRole.getName().equals(RoleType.ADMIN_SUPER.toString())) {
            System.out.println(currentRole.getName());
            throw new RuntimeException("Not a super admin role");
        }

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
        Role currentRole = superAdmin.getRole();

        AssignWarehouseResponseDTO responseDTO = new AssignWarehouseResponseDTO();

        if (!currentRole.getName().equals(RoleType.ADMIN_SUPER.toString())) {
            System.out.println(currentRole.getName());
            throw new RuntimeException("Not a super admin role");
        }

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
}
