package com.alejandro.lastmile.dto;

import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import java.time.LocalDateTime;

public record DeliveryResponse(
        Long id,
        String recipientName,
        String address,
        Double latitude,
        Double longitude,
        Double packageWeightKg,
        DeliveryPriority priority,
        DeliveryStatus status,
        LocalDateTime timeWindowStart,
        LocalDateTime timeWindowEnd,
        String notes,
        LocalDateTime createdAt
) {
}
