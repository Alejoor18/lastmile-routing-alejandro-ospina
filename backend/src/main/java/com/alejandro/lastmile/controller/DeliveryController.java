package com.alejandro.lastmile.controller;

import com.alejandro.lastmile.domain.enums.DeliveryPriority;
import com.alejandro.lastmile.domain.enums.DeliveryStatus;
import com.alejandro.lastmile.dto.DeliveryRequest;
import com.alejandro.lastmile.dto.DeliveryResponse;
import com.alejandro.lastmile.dto.StatusUpdateRequest;
import com.alejandro.lastmile.service.DeliveryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public List<DeliveryResponse> findAll(@RequestParam(required = false) DeliveryStatus status,
                                          @RequestParam(required = false) DeliveryPriority priority) {
        return deliveryService.findAll(status, priority);
    }

    @GetMapping("/{id}")
    public DeliveryResponse findById(@PathVariable Long id) {
        return deliveryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponse create(@Valid @RequestBody DeliveryRequest request) {
        return deliveryService.create(request);
    }

    @PutMapping("/{id}")
    public DeliveryResponse update(@PathVariable Long id, @Valid @RequestBody DeliveryRequest request) {
        return deliveryService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public DeliveryResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return deliveryService.updateStatus(id, deliveryService.parseStatus(request.status()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deliveryService.delete(id);
    }
}
