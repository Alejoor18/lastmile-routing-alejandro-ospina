package com.alejandro.lastmile.dto;

import com.alejandro.lastmile.domain.enums.UserRole;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String fullName,
        String email,
        UserRole role
) {
}
