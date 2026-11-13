package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;
import com.hr.freerein.dto.Requests.CaseRequest;
import com.hr.freerein.dto.Requests.NoteRequest;
import com.hr.freerein.dto.Requests.StatusRequest;
import com.hr.freerein.entity.Case;
import com.hr.freerein.entity.CaseNote;
import com.hr.freerein.entity.User;
import com.hr.freerein.exception.ResourceNotFoundException;
import com.hr.freerein.repository.CaseNoteRepository;
import com.hr.freerein.service.AccessService;
import com.hr.freerein.service.CaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/cases")
@Tag(name = "Cases", description = "Incident cases. Victims see their own; staff (admin/counsellor/legal advisor) see all.")
public class CaseController {

    private static final Set<String> STATUSES = Set.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED");

    private final CaseService caseService;
    private final CaseNoteRepository noteRepository;
    private final AccessService access;

    public CaseController(CaseService caseService, CaseNoteRepository noteRepository, AccessService access) {
        this.caseService = caseService;
        this.noteRepository = noteRepository;
        this.access = access;
    }

    @GetMapping
    @Operation(summary = "List cases (own cases, or all for staff)")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Case>>> list(Authentication auth,
                                                        @RequestParam(required = false) String status) {
        User me = access.currentUser(auth);
        List<Case> cases = access.isStaff(me)
                ? caseService.findAll().stream().filter(Case::isActive).toList()
                : caseService.findByUserId(me.getId()).stream().filter(Case::isActive).toList();
        if (status != null && !status.isBlank()) {
            cases = cases.stream().filter(c -> status.equalsIgnoreCase(c.getStatus())).toList();
        }
        return ResponseEntity.ok(ApiResponse.success(cases, "Cases retrieved"));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Case>> get(@PathVariable Long id, Authentication auth) {
        User me = access.currentUser(auth);
        Case c = load(id);
        access.requireOwnerOrStaff(me, c.getUser());
        return ResponseEntity.ok(ApiResponse.success(c, "Case retrieved"));
    }

    @PostMapping
    @Operation(summary = "Open a new case")
    public ResponseEntity<ApiResponse<Case>> create(@Valid @RequestBody CaseRequest req, Authentication auth) {
        User me = access.currentUser(auth);
        Case c = new Case();
        c.setUser(me);
        c.setTitle(req.title());
        c.setDescription(req.description());
        c.setNotes(req.notes());
        c.setStatus("OPEN");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(caseService.save(c)));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Case>> update(@PathVariable Long id, @Valid @RequestBody CaseRequest req,
                                                    Authentication auth) {
        User me = access.currentUser(auth);
        Case c = load(id);
        access.requireOwnerOrStaff(me, c.getUser());
        c.setTitle(req.title());
        c.setDescription(req.description());
        c.setNotes(req.notes());
        return ResponseEntity.ok(ApiResponse.updated(caseService.save(c)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change case status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)")
    @Transactional
    public ResponseEntity<ApiResponse<Case>> changeStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req,
                                                          Authentication auth) {
        User me = access.currentUser(auth);
        Case c = load(id);
        access.requireOwnerOrStaff(me, c.getUser());
        String status = req.status().trim().toUpperCase();
        if (!STATUSES.contains(status)) {
            throw new IllegalArgumentException("status must be one of " + STATUSES);
        }
        c.setStatus(status);
        return ResponseEntity.ok(ApiResponse.updated(caseService.save(c)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Archive a case (soft delete)")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        User me = access.currentUser(auth);
        Case c = load(id);
        if (!access.isOwner(me, c.getUser()) && !access.isAdmin(me)) {
            throw new AccessDeniedException("Only the owner or an admin can delete a case");
        }
        c.setActive(false);
        caseService.save(c);
        return ResponseEntity.ok(ApiResponse.deleted());
    }

    @GetMapping("/{id}/notes")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<CaseNote>>> notes(@PathVariable Long id, Authentication auth) {
        User me = access.currentUser(auth);
        Case c = load(id);
        access.requireOwnerOrStaff(me, c.getUser());
        return ResponseEntity.ok(ApiResponse.success(
                noteRepository.findByCaseEntityIdOrderByCreatedAtAsc(id), "Notes retrieved"));
    }

    @PostMapping("/{id}/notes")
    @Transactional
    public ResponseEntity<ApiResponse<CaseNote>> addNote(@PathVariable Long id, @Valid @RequestBody NoteRequest req,
                                                         Authentication auth) {
        User me = access.currentUser(auth);
        Case c = load(id);
        access.requireOwnerOrStaff(me, c.getUser());
        CaseNote n = new CaseNote();
        n.setCaseEntity(c);
        n.setUser(me);
        n.setNote(req.note());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(noteRepository.save(n)));
    }

    private Case load(Long id) {
        return caseService.findById(id).filter(Case::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + id));
    }
}
