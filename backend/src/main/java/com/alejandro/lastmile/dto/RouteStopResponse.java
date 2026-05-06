package com.alejandro.lastmile.dto;

import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import java.time.LocalDateTime;

public record RouteStopResponse(
        Long id,
        Long deliveryId,
        String recipientName,
        String address,
        Double latitude,
        Double longitude,
        Double packageWeightKg,
        DeliveryPriority priority,
        DeliveryStatus status,
        Integer stopOrder,
        Double distanceFromPreviousKm,
        LocalDateTime estimatedArrival,
        String notes
) {
}
