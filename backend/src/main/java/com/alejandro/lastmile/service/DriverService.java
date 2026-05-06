package com.alejandro.lastmile.service;

import com.alejandro.lastmile.domain.Driver;
import com.alejandro.lastmile.dto.DriverRequest;
import com.alejandro.lastmile.dto.DriverResponse;
import com.alejandro.lastmile.exception.ResourceNotFoundException;
import com.alejandro.lastmile.repository.DriverRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Transactional(readOnly = true)
    public List<DriverResponse> findAll() {
        return driverRepository.findAll().stream()
                .sorted(Comparator.comparing(Driver::getName))
                .map(DtoMapper::toDriverResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DriverResponse> findActive() {
        return driverRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(DtoMapper::toDriverResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DriverResponse findById(Long id) {
        return DtoMapper.toDriverResponse(getDriver(id));
    }

    @Transactional
    public DriverResponse create(DriverRequest request) {
        Driver driver = new Driver();
        apply(driver, request);
        return DtoMapper.toDriverResponse(driverRepository.save(driver));
    }

    @Transactional
    public DriverResponse update(Long id, DriverRequest request) {
        Driver driver = getDriver(id);
        apply(driver, request);
        return DtoMapper.toDriverResponse(driverRepository.save(driver));
    }

    @Transactional
    public void delete(Long id) {
        Driver driver = getDriver(id);
        driverRepository.delete(driver);
    }

    private Driver getDriver(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id " + id));
    }

    private void apply(Driver driver, DriverRequest request) {
        driver.setName(request.name().trim());
        driver.setPhone(request.phone().trim());
        driver.setVehiclePlate(request.vehiclePlate().trim().toUpperCase());
        driver.setVehicleType(request.vehicleType().trim());
        driver.setCapacityKg(request.capacityKg());
        driver.setActive(request.active() == null || request.active());
    }
}
