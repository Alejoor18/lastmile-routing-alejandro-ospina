package com.alejandro.lastmile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DriverRequest(
        @NotBlank(message = "El nombre del conductor es obligatorio")
        String name,

        @NotBlank(message = "El telefono es obligatorio")
        String phone,

        @NotBlank(message = "La placa es obligatoria")
        String vehiclePlate,

        @NotBlank(message = "El tipo de vehiculo es obligatorio")
        String vehicleType,

        @NotNull(message = "La capacidad es obligatoria")
        @Positive(message = "La capacidad debe ser mayor que cero")
        Double capacityKg,

        Boolean active
) {
}
