package com.alejandro.lastmile.domain;

import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String recipientName;

    @Column(nullable = false, length = 240)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double packageWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    private LocalDateTime timeWindowStart;

    private LocalDateTime timeWindowEnd;

    @Lob
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Delivery() {
    }

    public Delivery(String recipientName, String address, Double latitude, Double longitude, Double packageWeightKg,
                    DeliveryPriority priority, DeliveryStatus status, String notes) {
        this.recipientName = recipientName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.packageWeightKg = packageWeightKg;
        this.priority = priority;
        this.status = status;
        this.notes = notes;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = DeliveryStatus.PENDING;
        }
        if (priority == null) {
            priority = DeliveryPriority.MEDIUM;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getPackageWeightKg() {
        return packageWeightKg;
    }

    public void setPackageWeightKg(Double packageWeightKg) {
        this.packageWeightKg = packageWeightKg;
    }

    public DeliveryPriority getPriority() {
        return priority;
    }

    public void setPriority(DeliveryPriority priority) {
        this.priority = priority;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimeWindowStart() {
        return timeWindowStart;
    }

    public void setTimeWindowStart(LocalDateTime timeWindowStart) {
        this.timeWindowStart = timeWindowStart;
    }

    public LocalDateTime getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public void setTimeWindowEnd(LocalDateTime timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
