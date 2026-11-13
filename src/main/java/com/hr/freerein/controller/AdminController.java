package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;
import com.hr.freerein.dto.Requests.ActiveRequest;
import com.hr.freerein.dto.Requests.RoleRequest;
import com.hr.freerein.entity.Case;
import com.hr.freerein.entity.SupportRequest;
import com.hr.freerein.entity.User;
import com.hr.freerein.exception.ResourceNotFoundException;
import com.hr.freerein.repository.CaseRepository;
import com.hr.freerein.repository.ResourceRepository;
import com.hr.freerein.repository.SupportRequestRepository;
import com.hr.freerein.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Dashboard numbers and user management. ADMIN role only.")
public class AdminController {

    private final UserRepository users;
    private final CaseRepository cases;
    private final SupportRequestRepository supportRequests;
    private final ResourceRepository resources;

    public AdminController(UserRepository users, CaseRepository cases,
                           SupportRequestRepository supportRequests, ResourceRepository resources) {
        this.users = users;
        this.cases = cases;
        this.supportRequests = supportRequests;
        this.resources = resources;
    }

    @GetMapping("/stats")
    @Operation(summary = "Platform statistics for the admin dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats() {
        List<User> allUsers = users.findAll();
        List<Case> activeCases = cases.findByActiveTrueOrderByCreatedAtDesc();
        List<SupportRequest> activeRequests = supportRequests.findAll().stream()
                .filter(SupportRequest::isActive).toList();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalUsers", allUsers.size());
        out.put("usersByRole", group(allUsers, u -> String.valueOf(u.getRole())));
        out.put("totalCases", activeCases.size());
        out.put("casesByStatus", group(activeCases, Case::getStatus));
        out.put("totalSupportRequests", activeRequests.size());
        out.put("supportRequestsByStatus", group(activeRequests, SupportRequest::getStatus));
        out.put("activeResources", resources.findByActiveTrue().size());
        return ResponseEntity.ok(ApiResponse.success(out, "Statistics retrieved"));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> listUsers() {
        return ResponseEntity.ok(ApiResponse.success(users.findAll(), "Users retrieved"));
    }

    @PatchMapping("/users/{id}/active")
    @Operation(summary = "Enable or disable an account")
    public ResponseEntity<ApiResponse<User>> setActive(@PathVariable Long id, @RequestBody ActiveRequest req) {
        User u = find(id);
        u.setActive(req.active());
        return ResponseEntity.ok(ApiResponse.updated(users.save(u)));
    }

    @PatchMapping("/users/{id}/role")
    @Operation(summary = "Change a user's role (VICTIM, COUNSELLOR, LEGAL_ADVISOR, ADMIN)")
    public ResponseEntity<ApiResponse<User>> setRole(@PathVariable Long id, @Valid @RequestBody RoleRequest req) {
        User u = find(id);
        u.setRole(User.Role.valueOf(req.role().trim().toUpperCase()));
        return ResponseEntity.ok(ApiResponse.updated(users.save(u)));
    }

    private User find(Long id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private static <T> Map<String, Long> group(List<T> list, Function<T, String> key) {
        return list.stream().collect(Collectors.groupingBy(
                t -> key.apply(t) == null ? "UNKNOWN" : key.apply(t), TreeMap::new, Collectors.counting()));
    }
}
