package com.warehub.warehub.infrastructure.users.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.entity.enums.RolePermissions;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/role-test")
public class RoleTestController {

    // Endpoint to list all roles of the current user
    @GetMapping("/list")
    public ResponseEntity<?> roleTestList() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        return ApiResponse.successfulResponse("roles list: " + roles);
    }

    @PreAuthorize("hasAuthority('SCOPE_LOGGED_IN')")
    @GetMapping("/logged-in")
    public ResponseEntity<?> roleTestLoggedIn() {
        return ApiResponse.successfulResponse("user is logged in");
    }

    @PreAuthorize("hasAuthority('SCOPE_VERIFIED')")
    @GetMapping("/verified")
    public ResponseEntity<?> roleTestVerified() {
        return ApiResponse.successfulResponse("user is verified");
    }

    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER')")
    @GetMapping("/customer")
    public ResponseEntity<?> roleTestCustomer() {
        return ApiResponse.successfulResponse("customer access granted");
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN_WAREHOUSE')")
    @GetMapping("/admin-warehouse")
    public ResponseEntity<?> roleTestAdminWarehouse() {
        return ApiResponse.successfulResponse("admin warehouse access granted");
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN_SUPER')")
    @GetMapping("/admin-super")
    public ResponseEntity<?> roleTestAdminSuper() {
        return ApiResponse.successfulResponse("admin super access granted");
    }
}