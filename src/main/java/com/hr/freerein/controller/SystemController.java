package com.hr.freerein.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Tag(name = "System", description = "Landing info and health check (used by Docker / the hosting platform).")
public class SystemController {

    private final DataSource dataSource;

    @Value("${spring.application.name:FreeRein}")
    private String appName;

    public SystemController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    @Operation(summary = "API landing info")
    public Map<String, Object> root() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", appName + " backend");
        m.put("description", "REST API for the FreeRein domestic-violence support platform");
        m.put("docs", "/swagger-ui.html");
        m.put("health", "/api/health");
        return m;
    }

    @GetMapping("/api/health")
    @Operation(summary = "Health check including a live database ping")
    public Map<String, Object> health() {
        boolean dbUp;
        try (Connection c = dataSource.getConnection()) {
            dbUp = c.isValid(2);
        } catch (Exception e) {
            dbUp = false;
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", dbUp ? "UP" : "DEGRADED");
        m.put("database", dbUp ? "UP" : "DOWN");
        m.put("uptimeSeconds", ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        m.put("timestamp", Instant.now().toString());
        return m;
    }
}
