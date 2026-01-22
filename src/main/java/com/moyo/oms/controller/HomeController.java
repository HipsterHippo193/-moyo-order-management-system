package com.moyo.oms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        Map<String, String> response = Map.of(
            "message", "Welcome to Moyo Order Management System API",
            "health", "/api/health",
            "auth", "/api/auth/login",
            "documentation", "/swagger-ui.html"
        );
        return ResponseEntity.ok(response);
    }
}
