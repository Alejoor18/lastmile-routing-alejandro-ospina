package com.alejandro.lastmile.repository;

import com.alejandro.lastmile.domain.RoutePlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutePlanRepository extends JpaRepository<RoutePlan, Long> {
    @EntityGraph(attributePaths = {"driver", "stops", "stops.delivery"})
    List<RoutePlan> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"driver", "stops", "stops.delivery"})
    Optional<RoutePlan> findWithDetailsById(Long id);
}
