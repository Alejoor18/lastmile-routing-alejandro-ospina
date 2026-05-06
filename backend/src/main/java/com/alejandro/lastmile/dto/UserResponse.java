package com.alejandro.lastmile.dto;

import com.alejandro.lastmile.domain.enums.UserRole;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        LocalDateTime createdAt
) {
}
