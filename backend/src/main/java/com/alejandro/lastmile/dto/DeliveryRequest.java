package com.alejandro.lastmile.dto;

import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record DeliveryRequest(
        @NotBlank(message = "El destinatario es obligatorio")
        String recipientName,

        @NotBlank(message = "La direccion es obligatoria")
        String address,

        @NotNull(message = "La latitud es obligatoria")
        Double latitude,

        @NotNull(message = "La longitud es obligatoria")
        Double longitude,

        @NotNull(message = "El peso es obligatorio")
        @Positive(message = "El peso debe ser mayor que cero")
        Double packageWeightKg,

        DeliveryPriority priority,
        DeliveryStatus status,
        LocalDateTime timeWindowStart,
        LocalDateTime timeWindowEnd,
        String notes
) {
}
