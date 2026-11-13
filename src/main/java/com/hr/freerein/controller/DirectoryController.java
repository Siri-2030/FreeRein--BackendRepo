package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;
import com.hr.freerein.entity.User;
import com.hr.freerein.repository.UserRepository;
import com.hr.freerein.service.AccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Who can I talk to? Survivors see staff only; staff see survivors and other staff. Never exposes password or other private data. */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Directory", description = "People the current user may contact in Chat.")
public class DirectoryController {

    private final UserRepository users;
    private final AccessService access;

    public DirectoryController(UserRepository users, AccessService access) {
        this.users = users;
        this.access = access;
    }

    @GetMapping("/contacts")
    @Operation(summary = "Users the current user can message")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> contacts(Authentication auth) {
        User me = access.currentUser(auth);
        boolean meStaff = access.isStaff(me);
        List<Map<String, Object>> list = users.findByActiveTrue().stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .filter(u -> meStaff || access.isStaff(u))
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", u.getId());
                    m.put("name", u.getName());
                    m.put("email", u.getEmail());
                    m.put("role", u.getRole().name());
                    m.put("specialization", u.getSpecialization());
                    return m;
                })
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list, "Contacts retrieved"));
    }
}
