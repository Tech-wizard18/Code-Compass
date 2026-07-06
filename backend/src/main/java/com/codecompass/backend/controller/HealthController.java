package com.codecompass.backend.controller;

import com.codecompass.backend.dto.HealthStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public HealthStatus checkHelth(){
        return new HealthStatus("UP", "Code-Compass backend is runnign");
    }
}
