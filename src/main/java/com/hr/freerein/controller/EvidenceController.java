package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;
import com.hr.freerein.dto.Requests.EvidenceRequest;
import com.hr.freerein.entity.Evidence;
import com.hr.freerein.entity.User;
import com.hr.freerein.exception.ResourceNotFoundException;
import com.hr.freerein.repository.EvidenceRepository;
import com.hr.freerein.service.AccessService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Evidence vault: strictly private to the owner (another user's entry looks like a 404). */
@RestController
@RequestMapping("/api/evidence")
@Tag(name = "Evidence vault", description = "Private records of incidents. Owner only.")
public class EvidenceController {

    private final EvidenceRepository repository;
    private final AccessService access;

    public EvidenceController(EvidenceRepository repository, AccessService access) {
        this.repository = repository;
        this.access = access;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Evidence>>> list(Authentication auth) {
        User me = access.currentUser(auth);
        return ResponseEntity.ok(ApiResponse.success(
                repository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(me.getId()), "Evidence retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Evidence>> create(@Valid @RequestBody EvidenceRequest req, Authentication auth) {
        Evidence e = new Evidence();
        e.setUser(access.currentUser(auth));
        apply(e, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(repository.save(e)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Evidence>> update(@PathVariable Long id, @Valid @RequestBody EvidenceRequest req,
                                                        Authentication auth) {
        Evidence e = load(id, auth);
        apply(e, req);
        return ResponseEntity.ok(ApiResponse.updated(repository.save(e)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        Evidence e = load(id, auth);
        e.setActive(false);
        repository.save(e);
        return ResponseEntity.ok(ApiResponse.deleted());
    }

    private void apply(Evidence e, EvidenceRequest req) {
        e.setTitle(req.title());
        e.setDescription(req.description());
        e.setDateOfIncident(req.dateOfIncident() == null || req.dateOfIncident().isBlank() ? null : req.dateOfIncident());
        e.setFileName(req.fileName());
        e.setFileType(req.fileType() == null || req.fileType().isBlank() ? "other" : req.fileType());
    }

    private Evidence load(Long id, Authentication auth) {
        User me = access.currentUser(auth);
        return repository.findByIdAndUserId(id, me.getId()).filter(Evidence::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence not found: " + id));
    }
}
