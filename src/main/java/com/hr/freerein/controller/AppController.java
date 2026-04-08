package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/apps/public")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AppController {

    @GetMapping("/prod/public-settings/by-id/{appId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublicSettings(
            @PathVariable String appId,
            @RequestHeader(value = "X-App-Id", required = false) String appIdHeader) {
        
        Map<String, Object> settings = Map.of(
            "id", appId,
            "name", "FreeRein DV Support",
            "version", "1.0.0",
            "public_settings", Map.of(
                "auth_required", true,
                "features", List.of("emergency_help", "resources", "chat_support"),
                "hotline", "+1-800-799-7233",
                "emergency_message", "You're not alone. Help is available 24/7."
            ),
            "timestamp", LocalDateTime.now().toString()
        );
        
        // ✅ SINGLE LINE - Static factory method
        return ResponseEntity.ok(ApiResponse.success(settings, "Public settings retrieved"));
    }
}