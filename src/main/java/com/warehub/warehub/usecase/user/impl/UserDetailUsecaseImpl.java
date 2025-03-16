package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.WarehouseAdmin;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.users.dto.UserDetailRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseAdminRepository;
import com.warehub.warehub.usecase.user.UserDetailUsecase;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserDetailUsecaseImpl implements UserDetailUsecase {

    private final UsersRepository usersRepository;

    private final WarehouseAdminRepository warehouseAdminRepository;

    public UserDetailUsecaseImpl(UsersRepository usersRepository, WarehouseAdminRepository warehouseAdminRepository) {
        this.usersRepository = usersRepository;
        this.warehouseAdminRepository = warehouseAdminRepository;
    }

    @Override
    public UserDetailResponseDTO getUserDetail(JwtAuthenticationToken authToken) {
        User user = getUser(authToken);
        UserDetailResponseDTO response = new UserDetailResponseDTO().copyFromUser(user);
        if (user.getRole().getName().equals(RoleType.ADMIN_WAREHOUSE.toString())) {
            Optional<WarehouseAdmin> warehouseAdmin = warehouseAdminRepository.findByUserAssigneeId(user.getId());
            if (warehouseAdmin.isPresent()) {
                response.setWarehouseId(warehouseAdmin.get().getWarehouse().getId());
                response.setUserAssignerId(warehouseAdmin.get().getUserAssigner().getId());
            } else {
                response.setUserAssignerId(-1L);
                response.setWarehouseId(-1L);
            }
        } else {
            response.setWarehouseId(-1L);
            response.setUserAssignerId(-1L);
        }
        return response;
    }

    @Override
    public User getUser(JwtAuthenticationToken authToken) {
        Jwt jwt = authToken.getToken();
        Long id = jwt.getClaim("userId");
        return usersRepository.findById(id).get();
    }

    @Override
    public UserDetailResponseDTO updateUserDetail(JwtAuthenticationToken authToken, UserDetailRequestDTO req) {
        User user = getUser(authToken);
        if (req.getFullname() != null)
            user.setFullname(req.getFullname());
        if (req.getGender() != null) {
            String gender = req.getGender().trim();
            char firstChar = gender.isEmpty() ? 'X' : gender.charAt(0);
            switch (firstChar) {
                case 'M':
                    user.setGender("M");
                    break;
                case 'F':
                    user.setGender("F");
                    break;
                default:
                    user.setGender("X");
                    break;
            }
        }
        if (req.getBirthdate() != null)
            user.setBirthdate(req.getBirthdate());

        if (req.getBiodata() != null)
            user.setBiodata(req.getBiodata());
        if (req.getProfileImageUrl() != null)
            user.setProfileImageUrl(req.getProfileImageUrl());
        if (req.getPhoneNumber() != null)
            user.setPhoneNumber(req.getPhoneNumber());
        usersRepository.save(user);
        return getUserDetail(authToken);
    }
}
