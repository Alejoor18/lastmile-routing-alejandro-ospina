package com.alejandro.lastmile.service;

import com.alejandro.lastmile.domain.Delivery;
import com.alejandro.lastmile.domain.Driver;
import com.alejandro.lastmile.domain.RoutePlan;
import com.alejandro.lastmile.domain.RouteStop;
import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import com.alejandro.lastmile.domain.enums.RouteStatus;
import com.alejandro.lastmile.dto.RouteOptimizeRequest;
import com.alejandro.lastmile.dto.RoutePlanResponse;
import com.alejandro.lastmile.exception.BadRequestException;
import com.alejandro.lastmile.exception.ResourceNotFoundException;
import com.alejandro.lastmile.repository.DeliveryRepository;
import com.alejandro.lastmile.repository.DriverRepository;
import com.alejandro.lastmile.repository.RoutePlanRepository;
import com.alejandro.lastmile.util.GeoUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RouteOptimizationService {

    private static final double AVERAGE_SPEED_KMH = 30.0;
    private static final int SERVICE_MINUTES_PER_STOP = 8;

    private final DriverRepository driverRepository;
    private final DeliveryRepository deliveryRepository;
    private final RoutePlanRepository routePlanRepository;

    public RouteOptimizationService(DriverRepository driverRepository,
                                    DeliveryRepository deliveryRepository,
                                    RoutePlanRepository routePlanRepository) {
        this.driverRepository = driverRepository;
        this.deliveryRepository = deliveryRepository;
        this.routePlanRepository = routePlanRepository;
    }

    @Transactional
    public RoutePlanResponse optimize(RouteOptimizeRequest request) {
        validateWarehouse(request.warehouseLat(), request.warehouseLng());
        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id " + request.driverId()));
        if (!Boolean.TRUE.equals(driver.getActive())) {
            throw new BadRequestException("El conductor seleccionado no esta activo");
        }

        List<Delivery> candidates = loadCandidateDeliveries(request.deliveryIds());
        if (candidates.isEmpty()) {
            throw new BadRequestException("No existen entregas pendientes para optimizar");
        }

        List<OptimizedStop> orderedStops = nearestNeighbor(candidates, request.warehouseLat(), request.warehouseLng());
        double totalDistance = orderedStops.stream().mapToDouble(OptimizedStop::distanceFromPreviousKm).sum();
        int estimatedDuration = (int) Math.ceil((totalDistance / AVERAGE_SPEED_KMH) * 60.0
                + orderedStops.size() * SERVICE_MINUTES_PER_STOP);

        RoutePlan routePlan = new RoutePlan();
        routePlan.setName("Ruta " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        routePlan.setDriver(driver);
        routePlan.setWarehouseLat(request.warehouseLat());
        routePlan.setWarehouseLng(request.warehouseLng());
        routePlan.setTotalDistanceKm(round(totalDistance));
        routePlan.setEstimatedDurationMinutes(estimatedDuration);
        routePlan.setStatus(RouteStatus.PLANNED);

        LocalDateTime etaCursor = LocalDateTime.now();
        for (OptimizedStop optimizedStop : orderedStops) {
            int travelMinutes = (int) Math.ceil((optimizedStop.distanceFromPreviousKm() / AVERAGE_SPEED_KMH) * 60.0);
            etaCursor = etaCursor.plusMinutes(travelMinutes + SERVICE_MINUTES_PER_STOP);
            Delivery delivery = optimizedStop.delivery();
            delivery.setStatus(DeliveryStatus.ASSIGNED);
            RouteStop stop = new RouteStop(
                    delivery,
                    optimizedStop.stopOrder(),
                    round(optimizedStop.distanceFromPreviousKm()),
                    etaCursor,
                    "Parada generada por optimizacion nearest neighbor"
            );
            routePlan.addStop(stop);
        }

        RoutePlan saved = routePlanRepository.save(routePlan);
        return DtoMapper.toRoutePlanResponse(saved);
    }

    public RouteStatus parseStatus(String status) {
        try {
            return RouteStatus.valueOf(status.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new BadRequestException("Estado de ruta invalido: " + status);
        }
    }

    private List<Delivery> loadCandidateDeliveries(List<Long> deliveryIds) {
        if (deliveryIds == null || deliveryIds.isEmpty()) {
            return new ArrayList<>(deliveryRepository.findByStatusOrderByPriorityDescIdAsc(DeliveryStatus.PENDING));
        }

        Set<Long> uniqueIds = new HashSet<>(deliveryIds);
        List<Delivery> deliveries = deliveryRepository.findAllById(uniqueIds);
        if (deliveries.size() != uniqueIds.size()) {
            throw new ResourceNotFoundException("Una o mas entregas seleccionadas no existen");
        }

        List<Delivery> nonPending = deliveries.stream()
                .filter(delivery -> delivery.getStatus() != DeliveryStatus.PENDING)
                .toList();
        if (!nonPending.isEmpty()) {
            throw new BadRequestException("Solo se pueden optimizar entregas en estado PENDING");
        }

        return deliveries;
    }

    private List<OptimizedStop> nearestNeighbor(List<Delivery> candidates, double warehouseLat, double warehouseLng) {
        List<Delivery> remaining = new ArrayList<>(candidates);
        List<OptimizedStop> result = new ArrayList<>();
        double currentLat = warehouseLat;
        double currentLng = warehouseLng;
        int order = 1;

        while (!remaining.isEmpty()) {
            double fromLat = currentLat;
            double fromLng = currentLng;
            Delivery next = remaining.stream()
                    .min(Comparator
                            .comparingDouble((Delivery delivery) -> weightedScore(fromLat, fromLng, delivery))
                            .thenComparing((Delivery delivery) -> priorityRank(delivery.getPriority()), Comparator.reverseOrder())
                            .thenComparingDouble(delivery -> distanceFrom(fromLat, fromLng, delivery))
                            .thenComparing(Delivery::getId))
                    .orElseThrow();

            double distance = distanceFrom(currentLat, currentLng, next);
            result.add(new OptimizedStop(next, order, distance));
            remaining.remove(next);
            currentLat = next.getLatitude();
            currentLng = next.getLongitude();
            order++;
        }

        return result;
    }

    private double weightedScore(double fromLat, double fromLng, Delivery delivery) {
        double score = distanceFrom(fromLat, fromLng, delivery) - priorityBonus(delivery.getPriority());
        return Math.max(score, 0);
    }

    private double distanceFrom(double fromLat, double fromLng, Delivery delivery) {
        return GeoUtils.haversineKm(fromLat, fromLng, delivery.getLatitude(), delivery.getLongitude());
    }

    private double priorityBonus(DeliveryPriority priority) {
        if (priority == DeliveryPriority.HIGH) {
            return 1.5;
        }
        if (priority == DeliveryPriority.MEDIUM) {
            return 0.7;
        }
        return 0.0;
    }

    private int priorityRank(DeliveryPriority priority) {
        if (priority == DeliveryPriority.HIGH) {
            return 3;
        }
        if (priority == DeliveryPriority.MEDIUM) {
            return 2;
        }
        return 1;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void validateWarehouse(Double latitude, Double longitude) {
        if (!GeoUtils.isValidLatitude(latitude)) {
            throw new BadRequestException("La latitud del deposito debe estar entre -90 y 90");
        }
        if (!GeoUtils.isValidLongitude(longitude)) {
            throw new BadRequestException("La longitud del deposito debe estar entre -180 y 180");
        }
    }

    private record OptimizedStop(Delivery delivery, int stopOrder, double distanceFromPreviousKm) {
    }
}
