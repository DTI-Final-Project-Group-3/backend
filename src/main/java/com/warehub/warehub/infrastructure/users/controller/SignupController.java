package com.warehub.warehub.infrastructure.users.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.users.dto.CreateUserRequestDTO;
import com.warehub.warehub.usecase.user.CreateUserUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.warehub.warehub.entity.enums.RoleUtil.roleEnumFromString;

@RestController
@RequestMapping("/api/v1/signup")
public class SignupController {
    private final CreateUserUsecase createUserUsecase;

    public SignupController(CreateUserUsecase createUserUsecase) {
        this.createUserUsecase = createUserUsecase;
    }

    @PostMapping
    public ResponseEntity<?> createUserCustomer(@RequestBody CreateUserRequestDTO req,
                                                @RequestParam String role) {
        RoleType roleType = roleEnumFromString(role, RoleType.NOT_VERIFIED);
        var result = createUserUsecase.createUser(req, roleType);
        return ApiResponse.successfulResponse("Create new user "
                + roleType.toString() + " success", result);
    }
}
