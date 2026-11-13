package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;
import com.hr.freerein.dto.Requests.MessageRequest;
import com.hr.freerein.dto.Requests.SupportRequestCreate;
import com.hr.freerein.dto.Requests.SupportUpdate;
import com.hr.freerein.entity.SupportMessage;
import com.hr.freerein.entity.SupportRequest;
import com.hr.freerein.entity.User;
import com.hr.freerein.exception.ResourceNotFoundException;
import com.hr.freerein.service.AccessService;
import com.hr.freerein.service.SupportMessageService;
import com.hr.freerein.service.SupportRequestService;
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
@RequestMapping("/api/support/requests")
@Tag(name = "Support", description = "Help requests and the chat thread between a victim and a counsellor / legal advisor.")
public class SupportController {

    private static final Set<String> STATUSES = Set.of("PENDING", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "CLOSED");
    private static final Set<String> URGENCIES = Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");

    private final SupportRequestService requests;
    private final SupportMessageService messages;
    private final AccessService access;

    public SupportController(SupportRequestService requests, SupportMessageService messages, AccessService access) {
        this.requests = requests;
        this.messages = messages;
        this.access = access;
    }

    @GetMapping
    @Operation(summary = "List support requests (own, or all for staff). Optional ?status=PENDING")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<SupportRequest>>> list(Authentication auth,
                                                                  @RequestParam(required = false) String status) {
        User me = access.currentUser(auth);
        List<SupportRequest> list = access.isStaff(me)
                ? requests.findAll().stream().filter(SupportRequest::isActive).toList()
                : requests.findByUserId(me.getId()).stream().filter(SupportRequest::isActive).toList();
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(r -> status.equalsIgnoreCase(r.getStatus())).toList();
        }
        return ResponseEntity.ok(ApiResponse.success(list, "Support requests retrieved"));
    }

    @PostMapping
    @Operation(summary = "Ask for help")
    public ResponseEntity<ApiResponse<SupportRequest>> create(@Valid @RequestBody SupportRequestCreate req,
                                                              Authentication auth) {
        SupportRequest r = new SupportRequest();
        r.setUser(access.currentUser(auth));
        r.setSubject(req.subject());
        r.setDescription(req.description());
        r.setType(req.requestType() == null || req.requestType().isBlank() ? "general" : req.requestType());
        String urgency = req.urgency() == null ? "MEDIUM" : req.urgency().trim().toUpperCase();
        r.setUrgency(URGENCIES.contains(urgency) ? urgency : "MEDIUM");
        r.setAnonymous(req.anonymous());
        r.setStatus("PENDING");
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(requests.save(r)));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<SupportRequest>> get(@PathVariable Long id, Authentication auth) {
        User me = access.currentUser(auth);
        SupportRequest r = load(id);
        access.requireOwnerOrStaff(me, r.getUser());
        return ResponseEntity.ok(ApiResponse.success(r, "Support request retrieved"));
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Staff: take this request (assigns you and moves it to IN_PROGRESS)")
    @Transactional
    public ResponseEntity<ApiResponse<SupportRequest>> assign(@PathVariable Long id, Authentication auth) {
        User me = access.currentUser(auth);
        if (!access.isStaff(me)) {
            throw new AccessDeniedException("Only staff can take a request");
        }
        SupportRequest r = load(id);
        r.setAssignedSupporter(me.getEmail());
        r.setStatus("IN_PROGRESS");
        return ResponseEntity.ok(ApiResponse.updated(requests.save(r)));
    }

    /**
     * Partial update. Owner or staff may change the status; only staff may assign or add notes.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update status / assignment / notes (fields left out are unchanged)")
    @Transactional
    public ResponseEntity<ApiResponse<SupportRequest>> update(@PathVariable Long id, @RequestBody SupportUpdate req,
                                                              Authentication auth) {
        User me = access.currentUser(auth);
        SupportRequest r = load(id);
        access.requireOwnerOrStaff(me, r.getUser());

        if (req.status() != null && !req.status().isBlank()) {
            String status = req.status().trim().toUpperCase();
            if (!STATUSES.contains(status)) {
                throw new IllegalArgumentException("status must be one of " + STATUSES);
            }
            r.setStatus(status);
        }
        if (req.assignedTo() != null || req.notes() != null) {
            if (!access.isStaff(me)) {
                throw new AccessDeniedException("Only staff can assign a request or add notes");
            }
            if (req.assignedTo() != null) {
                r.setAssignedSupporter(req.assignedTo().isBlank() ? null : req.assignedTo().trim().toLowerCase());
            }
            if (req.notes() != null) {
                r.setNotes(req.notes());
            }
        }
        return ResponseEntity.ok(ApiResponse.updated(requests.save(r)));
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Chat thread of a request (internal staff notes are hidden from the victim)")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<SupportMessage>>> messages(@PathVariable Long id, Authentication auth) {
        User me = access.currentUser(auth);
        SupportRequest r = load(id);
        access.requireOwnerOrStaff(me, r.getUser());
        boolean staff = access.isStaff(me);
        List<SupportMessage> list = messages.findBySupportRequestId(id).stream()
                .filter(SupportMessage::isActive)
                .filter(m -> staff || !m.isInternal())
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list, "Messages retrieved"));
    }

    @PostMapping("/{id}/messages")
    @Transactional
    public ResponseEntity<ApiResponse<SupportMessage>> send(@PathVariable Long id,
                                                            @Valid @RequestBody MessageRequest req,
                                                            Authentication auth) {
        User me = access.currentUser(auth);
        SupportRequest r = load(id);
        access.requireOwnerOrStaff(me, r.getUser());
        boolean staff = access.isStaff(me);
        boolean owner = access.isOwner(me, r.getUser());

        SupportMessage m = new SupportMessage();
        m.setSupportRequest(r);
        m.setSenderId(String.valueOf(me.getId()));
        m.setSenderEmail(me.getEmail());
        m.setSenderRole(owner ? "victim" : me.getRole().name().toLowerCase());
        m.setContent(req.content());
        m.setFromSupporter(staff && !owner);
        m.setInternal(staff && !owner && Boolean.TRUE.equals(req.internal()));   // victims can never write internal notes
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(messages.save(m)));
    }

    private SupportRequest load(Long id) {
        return requests.findById(id).filter(SupportRequest::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Support request not found: " + id));
    }
}
