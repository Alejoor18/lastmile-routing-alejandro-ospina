package com.alejandro.lastmile.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RouteOptimizeRequest(
        @NotNull(message = "El conductor es obligatorio")
        Long driverId,

        @NotNull(message = "La latitud del deposito es obligatoria")
        Double warehouseLat,

        @NotNull(message = "La longitud del deposito es obligatoria")
        Double warehouseLng,

        List<Long> deliveryIds
) {
}
