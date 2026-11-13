package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;
import com.hr.freerein.dto.Requests.SafetyPlanRequest;
import com.hr.freerein.entity.SafetyPlan;
import com.hr.freerein.entity.User;
import com.hr.freerein.exception.ResourceNotFoundException;
import com.hr.freerein.repository.SafetyPlanRepository;
import com.hr.freerein.service.AccessService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Safety plans are deliberately PRIVATE: only the owner can ever read or change them (not even staff). */
@RestController
@RequestMapping("/api/safety-plans")
@Tag(name = "Safety plans", description = "Private to the person who created them.")
public class SafetyPlanController {

    private final SafetyPlanRepository repository;
    private final AccessService access;

    public SafetyPlanController(SafetyPlanRepository repository, AccessService access) {
        this.repository = repository;
        this.access = access;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SafetyPlan>>> list(Authentication auth) {
        User me = access.currentUser(auth);
        return ResponseEntity.ok(ApiResponse.success(
                repository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(me.getId()), "Safety plans retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SafetyPlan>> get(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(load(id, auth), "Safety plan retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SafetyPlan>> create(@Valid @RequestBody SafetyPlanRequest req, Authentication auth) {
        SafetyPlan p = new SafetyPlan();
        p.setUser(access.currentUser(auth));
        apply(p, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(repository.save(p)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SafetyPlan>> update(@PathVariable Long id, @Valid @RequestBody SafetyPlanRequest req,
                                                          Authentication auth) {
        SafetyPlan p = load(id, auth);
        apply(p, req);
        return ResponseEntity.ok(ApiResponse.updated(repository.save(p)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        SafetyPlan p = load(id, auth);
        p.setActive(false);
        repository.save(p);
        return ResponseEntity.ok(ApiResponse.deleted());
    }

    private void apply(SafetyPlan p, SafetyPlanRequest req) {
        p.setTitle(req.title());
        p.setSteps(req.steps());
        p.setEmergencyContacts(req.emergencyContacts());
        p.setNotes(req.notes());
        p.setSafePlaces(req.safePlaces());
        p.setImportantDocuments(req.importantDocuments());
        p.setSafetySteps(req.safetySteps());
        p.setSupportServices(req.supportServices());
        p.setPersonalNotes(req.personalNotes());
    }

    /** Someone else's plan looks exactly like a missing one (404), so its existence is not leaked. */
    private SafetyPlan load(Long id, Authentication auth) {
        User me = access.currentUser(auth);
        return repository.findByIdAndUserId(id, me.getId()).filter(SafetyPlan::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Safety plan not found: " + id));
    }
}
