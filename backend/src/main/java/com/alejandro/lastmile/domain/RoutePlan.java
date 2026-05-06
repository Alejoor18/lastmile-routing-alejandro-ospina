package com.alejandro.lastmile.domain;

import com.alejandro.lastmile.domain.enums.RouteStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "route_plans")
public class RoutePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Double warehouseLat;

    @Column(nullable = false)
    private Double warehouseLng;

    @Column(nullable = false)
    private Double totalDistanceKm;

    @Column(nullable = false)
    private Integer estimatedDurationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RouteStatus status;

    @OneToMany(mappedBy = "routePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopOrder ASC")
    private List<RouteStop> stops = new ArrayList<>();

    public RoutePlan() {
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RouteStatus.PLANNED;
        }
    }

    public void addStop(RouteStop stop) {
        stops.add(stop);
        stop.setRoutePlan(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getWarehouseLat() {
        return warehouseLat;
    }

    public void setWarehouseLat(Double warehouseLat) {
        this.warehouseLat = warehouseLat;
    }

    public Double getWarehouseLng() {
        return warehouseLng;
    }

    public void setWarehouseLng(Double warehouseLng) {
        this.warehouseLng = warehouseLng;
    }

    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public RouteStatus getStatus() {
        return status;
    }

    public void setStatus(RouteStatus status) {
        this.status = status;
    }

    public List<RouteStop> getStops() {
        return stops;
    }

    public void setStops(List<RouteStop> stops) {
        this.stops = stops;
    }
}
