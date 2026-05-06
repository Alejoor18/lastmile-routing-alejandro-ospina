package com.alejandro.lastmile.service;

import com.alejandro.lastmile.domain.Delivery;
import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import com.alejandro.lastmile.dto.DeliveryRequest;
import com.alejandro.lastmile.dto.DeliveryResponse;
import com.alejandro.lastmile.exception.BadRequestException;
import com.alejandro.lastmile.exception.ResourceNotFoundException;
import com.alejandro.lastmile.repository.DeliveryRepository;
import com.alejandro.lastmile.util.GeoUtils;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> findAll(DeliveryStatus status, DeliveryPriority priority) {
        List<Delivery> deliveries;
        if (status != null && priority != null) {
            deliveries = deliveryRepository.findByStatusAndPriorityOrderByIdAsc(status, priority);
        } else if (status != null) {
            deliveries = deliveryRepository.findByStatusOrderByPriorityDescIdAsc(status);
        } else if (priority != null) {
            deliveries = deliveryRepository.findByPriorityOrderByIdAsc(priority);
        } else {
            deliveries = deliveryRepository.findAll().stream()
                    .sorted(Comparator.comparing(Delivery::getId))
                    .toList();
        }

        return deliveries.stream().map(DtoMapper::toDeliveryResponse).toList();
    }

    @Transactional(readOnly = true)
    public DeliveryResponse findById(Long id) {
        return DtoMapper.toDeliveryResponse(getDelivery(id));
    }

    @Transactional
    public DeliveryResponse create(DeliveryRequest request) {
        Delivery delivery = new Delivery();
        apply(delivery, request);
        return DtoMapper.toDeliveryResponse(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponse update(Long id, DeliveryRequest request) {
        Delivery delivery = getDelivery(id);
        apply(delivery, request);
        return DtoMapper.toDeliveryResponse(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponse updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = getDelivery(id);
        delivery.setStatus(status);
        return DtoMapper.toDeliveryResponse(deliveryRepository.save(delivery));
    }

    @Transactional
    public void delete(Long id) {
        Delivery delivery = getDelivery(id);
        deliveryRepository.delete(delivery);
    }

    public DeliveryStatus parseStatus(String status) {
        try {
            return DeliveryStatus.valueOf(status.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new BadRequestException("Estado de entrega invalido: " + status);
        }
    }

    private Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada con id " + id));
    }

    private void apply(Delivery delivery, DeliveryRequest request) {
        validate(request);
        delivery.setRecipientName(request.recipientName().trim());
        delivery.setAddress(request.address().trim());
        delivery.setLatitude(request.latitude());
        delivery.setLongitude(request.longitude());
        delivery.setPackageWeightKg(request.packageWeightKg());
        delivery.setPriority(request.priority() == null ? DeliveryPriority.MEDIUM : request.priority());
        delivery.setStatus(request.status() == null ? DeliveryStatus.PENDING : request.status());
        delivery.setTimeWindowStart(request.timeWindowStart());
        delivery.setTimeWindowEnd(request.timeWindowEnd());
        delivery.setNotes(request.notes());
    }

    private void validate(DeliveryRequest request) {
        if (!GeoUtils.isValidLatitude(request.latitude())) {
            throw new BadRequestException("La latitud debe estar entre -90 y 90");
        }
        if (!GeoUtils.isValidLongitude(request.longitude())) {
            throw new BadRequestException("La longitud debe estar entre -180 y 180");
        }
        if (request.timeWindowStart() != null && request.timeWindowEnd() != null
                && request.timeWindowEnd().isBefore(request.timeWindowStart())) {
            throw new BadRequestException("La ventana horaria final no puede ser anterior a la inicial");
        }
    }
}
