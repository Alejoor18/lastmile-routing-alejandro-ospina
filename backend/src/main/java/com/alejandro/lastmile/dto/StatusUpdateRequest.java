package com.alejandro.lastmile.dto;

import jakarta.validation.constraints.NotBlank;

public record StatusUpdateRequest(
        @NotBlank(message = "El estado es obligatorio")
        String status
) {
}
