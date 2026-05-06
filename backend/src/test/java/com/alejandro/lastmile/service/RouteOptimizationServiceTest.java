package com.alejandro.lastmile.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.alejandro.lastmile.domain.Delivery;
import com.alejandro.lastmile.domain.Driver;
import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import com.alejandro.lastmile.dto.RouteOptimizeRequest;
import com.alejandro.lastmile.dto.RoutePlanResponse;
import com.alejandro.lastmile.repository.DeliveryRepository;
import com.alejandro.lastmile.repository.DriverRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RouteOptimizationServiceTest {

    @Autowired
    private RouteOptimizationService routeOptimizationService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAll();
        driverRepository.deleteAll();
    }

    @Test
    void optimizesPendingDeliveriesAndMarksThemAssigned() {
        Driver driver = driverRepository.save(new Driver("Ruta Tester", "300", "TST-001", "Van", 500.0, true));
        Delivery lowNearby = deliveryRepository.save(new Delivery("Cliente Low", "Cerca", 6.2450, -75.5810, 2.0,
                DeliveryPriority.LOW, DeliveryStatus.PENDING, null));
        Delivery highSlightlyFar = deliveryRepository.save(new Delivery("Cliente High", "Alta", 6.2500, -75.5810, 2.0,
                DeliveryPriority.HIGH, DeliveryStatus.PENDING, null));
        Delivery medium = deliveryRepository.save(new Delivery("Cliente Medium", "Media", 6.2600, -75.5900, 2.0,
                DeliveryPriority.MEDIUM, DeliveryStatus.PENDING, null));

        RoutePlanResponse response = routeOptimizationService.optimize(new RouteOptimizeRequest(
                driver.getId(),
                6.2442,
                -75.5812,
                List.of(lowNearby.getId(), highSlightlyFar.getId(), medium.getId())
        ));

        assertThat(response.id()).isNotNull();
        assertThat(response.stops()).hasSize(3);
        assertThat(response.stops().getFirst().deliveryId()).isEqualTo(highSlightlyFar.getId());
        assertThat(response.totalDistanceKm()).isGreaterThan(0);
        assertThat(response.estimatedDurationMinutes()).isGreaterThanOrEqualTo(24);

        List<Delivery> savedDeliveries = deliveryRepository.findAllById(List.of(
                lowNearby.getId(),
                highSlightlyFar.getId(),
                medium.getId()
        ));
        assertThat(savedDeliveries).allMatch(delivery -> delivery.getStatus() == DeliveryStatus.ASSIGNED);
    }
}
