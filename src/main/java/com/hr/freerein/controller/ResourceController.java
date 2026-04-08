package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;
import com.hr.freerein.dto.QueryParamsDto;
import com.hr.freerein.entity.Resource;
import com.hr.freerein.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = {"http://localhost:3000"})
public class ResourceController {
    
    @Autowired  // ✅ Simple @Autowired
    private ResourceService resourceService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Resource>>> getAllResources() {
        List<Resource> resources = resourceService.getAllActiveResources();
        return ResponseEntity.ok(ApiResponse.success(resources, "Resources retrieved"));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Resource>>> getResourcesByCategory(@PathVariable String category) {
        List<Resource> resources = resourceService.getResourcesByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(resources, "Category resources"));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Resource>>> searchResources(@Valid @RequestBody QueryParamsDto params) {
        List<Resource> resources = resourceService.searchResources(params.getSearch());
        return ResponseEntity.ok(ApiResponse.success(resources, "Search results"));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Resource>> createResource(@RequestBody Resource resource) {
        Resource saved = resourceService.createResource(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(saved));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Resource>> updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        Resource updated = resourceService.updateResource(id, resource);
        return ResponseEntity.ok(ApiResponse.updated(updated));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}