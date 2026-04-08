
package com.hr.freerein.service;

import com.hr.freerein.entity.Resource;
import com.hr.freerein.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResourceService extends BaseEntityService<Resource, Long, ResourceRepository> {

    @Autowired
    public ResourceService(ResourceRepository repository) {
        super(repository);
    }

    // ✅ IMPLEMENTED: Controller calls this
    public List<Resource> getAllActiveResources() {
        return repository.findByActiveTrue();
    }

    // ✅ IMPLEMENTED: Controller calls this
    public List<Resource> getResourcesByCategory(String category) {
        return repository.findByCategoryAndActiveTrue(category);
    }

    // ✅ IMPLEMENTED: Controller calls this
    public List<Resource> searchResources(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllActiveResources();
        }
        return repository.searchResources(searchTerm);
    }

    // ✅ CRUD Methods
    public Resource createResource(Resource resource) {
        resource.setActive(true);  // Default active
        return repository.save(resource);
    }

    public Resource updateResource(Long id, Resource resourceDetails) {
        Resource resource = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resource not found: " + id));
        
        resource.setTitle(resourceDetails.getTitle());
        resource.setDescription(resourceDetails.getDescription());
        resource.setCategory(resourceDetails.getCategory());
        //resource.setContent(resourceDetails.getContent());
        //resource.setContactInfo(resourceDetails.getContactInfo());
        //resource.setLocation(resourceDetails.getLocation());
        resource.setActive(resourceDetails.isActive());
        
        return repository.save(resource);
    }

    public void deleteResource(Long id) {
        Resource resource = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resource not found: " + id));
        resource.setActive(false);  // Soft delete
        repository.save(resource);
    }
}