package com.alejandro.lastmile.dto;

public record DriverResponse(
        Long id,
        String name,
        String phone,
        String vehiclePlate,
        String vehicleType,
        Double capacityKg,
        Boolean active
) {
}
