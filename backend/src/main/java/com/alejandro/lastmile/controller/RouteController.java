package com.alejandro.lastmile.controller;

import com.alejandro.lastmile.dto.RouteOptimizeRequest;
import com.alejandro.lastmile.dto.RoutePlanResponse;
import com.alejandro.lastmile.dto.StatusUpdateRequest;
import com.alejandro.lastmile.service.RouteOptimizationService;
import com.alejandro.lastmile.service.RoutePlanService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteOptimizationService routeOptimizationService;
    private final RoutePlanService routePlanService;

    public RouteController(RouteOptimizationService routeOptimizationService, RoutePlanService routePlanService) {
        this.routeOptimizationService = routeOptimizationService;
        this.routePlanService = routePlanService;
    }

    @PostMapping("/optimize")
    @ResponseStatus(HttpStatus.CREATED)
    public RoutePlanResponse optimize(@Valid @RequestBody RouteOptimizeRequest request) {
        return routeOptimizationService.optimize(request);
    }

    @GetMapping
    public List<RoutePlanResponse> findAll() {
        return routePlanService.findAll();
    }

    @GetMapping("/{id}")
    public RoutePlanResponse findById(@PathVariable Long id) {
        return routePlanService.findById(id);
    }

    @PatchMapping("/{id}/status")
    public RoutePlanResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return routePlanService.updateStatus(id, routeOptimizationService.parseStatus(request.status()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        routePlanService.delete(id);
    }
}
