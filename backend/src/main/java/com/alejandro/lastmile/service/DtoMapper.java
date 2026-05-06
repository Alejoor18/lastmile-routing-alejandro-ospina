package com.alejandro.lastmile.service;

import com.alejandro.lastmile.domain.Delivery;
import com.alejandro.lastmile.domain.Driver;
import com.alejandro.lastmile.domain.RoutePlan;
import com.alejandro.lastmile.domain.RouteStop;
import com.alejandro.lastmile.domain.User;
import com.alejandro.lastmile.dto.DeliveryResponse;
import com.alejandro.lastmile.dto.DriverResponse;
import com.alejandro.lastmile.dto.RoutePlanResponse;
import com.alejandro.lastmile.dto.RouteStopResponse;
import com.alejandro.lastmile.dto.UserResponse;
import java.util.Comparator;
import java.util.List;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

    public static DriverResponse toDriverResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getPhone(),
                driver.getVehiclePlate(),
                driver.getVehicleType(),
                driver.getCapacityKg(),
                driver.getActive()
        );
    }

    public static DeliveryResponse toDeliveryResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getRecipientName(),
                delivery.getAddress(),
                delivery.getLatitude(),
                delivery.getLongitude(),
                delivery.getPackageWeightKg(),
                delivery.getPriority(),
                delivery.getStatus(),
                delivery.getTimeWindowStart(),
                delivery.getTimeWindowEnd(),
                delivery.getNotes(),
                delivery.getCreatedAt()
        );
    }

    public static RoutePlanResponse toRoutePlanResponse(RoutePlan routePlan) {
        List<RouteStopResponse> stops = routePlan.getStops().stream()
                .sorted(Comparator.comparing(RouteStop::getStopOrder))
                .map(DtoMapper::toRouteStopResponse)
                .toList();

        return new RoutePlanResponse(
                routePlan.getId(),
                routePlan.getName(),
                toDriverResponse(routePlan.getDriver()),
                routePlan.getCreatedAt(),
                routePlan.getWarehouseLat(),
                routePlan.getWarehouseLng(),
                routePlan.getTotalDistanceKm(),
                routePlan.getEstimatedDurationMinutes(),
                routePlan.getStatus(),
                stops
        );
    }

    public static RouteStopResponse toRouteStopResponse(RouteStop stop) {
        Delivery delivery = stop.getDelivery();
        return new RouteStopResponse(
                stop.getId(),
                delivery.getId(),
                delivery.getRecipientName(),
                delivery.getAddress(),
                delivery.getLatitude(),
                delivery.getLongitude(),
                delivery.getPackageWeightKg(),
                delivery.getPriority(),
                delivery.getStatus(),
                stop.getStopOrder(),
                stop.getDistanceFromPreviousKm(),
                stop.getEstimatedArrival(),
                stop.getNotes()
        );
    }
}
