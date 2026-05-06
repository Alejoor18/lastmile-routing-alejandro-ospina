package com.alejandro.lastmile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 40)
    private String phone;

    @Column(nullable = false, length = 20)
    private String vehiclePlate;

    @Column(nullable = false, length = 50)
    private String vehicleType;

    @Column(nullable = false)
    private Double capacityKg;

    @Column(nullable = false)
    private Boolean active = true;

    public Driver() {
    }

    public Driver(String name, String phone, String vehiclePlate, String vehicleType, Double capacityKg, Boolean active) {
        this.name = name;
        this.phone = phone;
        this.vehiclePlate = vehiclePlate;
        this.vehicleType = vehicleType;
        this.capacityKg = capacityKg;
        this.active = active;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getCapacityKg() {
        return capacityKg;
    }

    public void setCapacityKg(Double capacityKg) {
        this.capacityKg = capacityKg;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
