package com.alejandro.lastmile.repository;

import com.alejandro.lastmile.domain.Delivery;
import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByStatusOrderByPriorityDescIdAsc(DeliveryStatus status);

    List<Delivery> findByPriorityOrderByIdAsc(DeliveryPriority priority);

    List<Delivery> findByStatusAndPriorityOrderByIdAsc(DeliveryStatus status, DeliveryPriority priority);

    long countByStatus(DeliveryStatus status);
}
