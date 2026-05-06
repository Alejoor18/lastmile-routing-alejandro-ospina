package com.alejandro.lastmile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "route_stops")
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_plan_id", nullable = false)
    private RoutePlan routePlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(nullable = false)
    private Double distanceFromPreviousKm;

    private LocalDateTime estimatedArrival;

    @Lob
    private String notes;

    public RouteStop() {
    }

    public RouteStop(Delivery delivery, Integer stopOrder, Double distanceFromPreviousKm,
                     LocalDateTime estimatedArrival, String notes) {
        this.delivery = delivery;
        this.stopOrder = stopOrder;
        this.distanceFromPreviousKm = distanceFromPreviousKm;
        this.estimatedArrival = estimatedArrival;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoutePlan getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(RoutePlan routePlan) {
        this.routePlan = routePlan;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Integer getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(Integer stopOrder) {
        this.stopOrder = stopOrder;
    }

    public Double getDistanceFromPreviousKm() {
        return distanceFromPreviousKm;
    }

    public void setDistanceFromPreviousKm(Double distanceFromPreviousKm) {
        this.distanceFromPreviousKm = distanceFromPreviousKm;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
