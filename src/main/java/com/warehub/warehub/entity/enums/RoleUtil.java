package com.warehub.warehub.entity.enums;

public class RoleUtil {
    public static RoleType roleEnumFromString(String input, RoleType defaultRole) {
        try {
            return RoleType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultRole;
        }
    }
}
