package com.alejandro.lastmile.service;

import com.alejandro.lastmile.domain.RoutePlan;
import com.alejandro.lastmile.domain.enums.RouteStatus;
import com.alejandro.lastmile.dto.RoutePlanResponse;
import com.alejandro.lastmile.exception.ResourceNotFoundException;
import com.alejandro.lastmile.repository.RoutePlanRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoutePlanService {

    private final RoutePlanRepository routePlanRepository;

    public RoutePlanService(RoutePlanRepository routePlanRepository) {
        this.routePlanRepository = routePlanRepository;
    }

    @Transactional(readOnly = true)
    public List<RoutePlanResponse> findAll() {
        return routePlanRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(DtoMapper::toRoutePlanResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoutePlanResponse findById(Long id) {
        return DtoMapper.toRoutePlanResponse(getRoutePlan(id));
    }

    @Transactional
    public RoutePlanResponse updateStatus(Long id, RouteStatus status) {
        RoutePlan routePlan = getRoutePlan(id);
        routePlan.setStatus(status);
        return DtoMapper.toRoutePlanResponse(routePlanRepository.save(routePlan));
    }

    @Transactional
    public void delete(Long id) {
        RoutePlan routePlan = getRoutePlan(id);
        routePlanRepository.delete(routePlan);
    }

    private RoutePlan getRoutePlan(Long id) {
        return routePlanRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada con id " + id));
    }
}
