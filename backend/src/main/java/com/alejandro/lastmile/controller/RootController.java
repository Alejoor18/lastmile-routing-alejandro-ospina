package com.alejandro.lastmile.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
                "app", "LastMile Optimizer",
                "student", "Alejandro Ospina",
                "status", "UP"
        );
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "lastmile-backend"
        );
    }
}
