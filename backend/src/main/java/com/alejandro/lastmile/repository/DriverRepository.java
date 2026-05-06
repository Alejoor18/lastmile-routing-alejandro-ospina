package com.alejandro.lastmile.repository;

import com.alejandro.lastmile.domain.Driver;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByActiveTrueOrderByNameAsc();
}
