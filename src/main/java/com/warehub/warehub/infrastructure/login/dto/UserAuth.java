package com.warehub.warehub.infrastructure.login.dto;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.entity.enums.RolePermissions;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static com.warehub.warehub.entity.enums.RoleUtil.roleEnumFromString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth implements UserDetails {
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<GrantedAuthority>();
        RoleType roleType = roleEnumFromString(this.user.getRole().getName(), RoleType.NOT_VERIFIED);

        // Map roles to their permissions
        Map<RoleType, List<RolePermissions>> rolePermissionsMap = Map.of(
                RoleType.NOT_VERIFIED, List.of(RolePermissions.LOGGED_IN),
                RoleType.CUSTOMER_VERIFIED, List.of(RolePermissions.LOGGED_IN, RolePermissions.VERIFIED, RolePermissions.CUSTOMER),
                RoleType.ADMIN_WAREHOUSE, List.of(RolePermissions.LOGGED_IN, RolePermissions.VERIFIED, RolePermissions.ADMIN_WAREHOUSE),
                RoleType.ADMIN_SUPER, List.of(RolePermissions.LOGGED_IN, RolePermissions.VERIFIED, RolePermissions.ADMIN_WAREHOUSE, RolePermissions.ADMIN_SUPER)
        );

        // Add authorities based on role
        rolePermissionsMap.getOrDefault(roleType, List.of()).forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.toString()))
        );

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    public Long getUserId() {
        return this.user.getId();
    }

    @Override
    public String getPassword() {
        return this.user.getPasswordHash();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public static User getCurrentUser(UsersRepository usersRepository) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Jwt jwt) {
            Long userId = jwt.getClaim("userId"); // Ensure your JWT includes the 'email' claim

            Optional<User> user = usersRepository.findById(userId);
            if (!user.isEmpty()) {
                return user.get();
            }
        }
        return null; // Or throw an exception if necessary
    }
}