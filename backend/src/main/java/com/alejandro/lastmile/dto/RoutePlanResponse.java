package com.alejandro.lastmile.dto;

import com.alejandro.lastmile.domain.enums.RouteStatus;
import java.time.LocalDateTime;
import java.util.List;

public record RoutePlanResponse(
        Long id,
        String name,
        DriverResponse driver,
        LocalDateTime createdAt,
        Double warehouseLat,
        Double warehouseLng,
        Double totalDistanceKm,
        Integer estimatedDurationMinutes,
        RouteStatus status,
        List<RouteStopResponse> stops
) {
}
