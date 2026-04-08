package com.hr.freerein.repository;

import com.hr.freerein.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends BaseRepository<Resource, Long> {
    
	 List<Resource> findByActiveTrue();  // Changed from findByIsActiveTrue()
	 List<Resource> findByCategoryAndActiveTrue(String category);  // Changed from findByCategoryAndActiveTrue()
	    
    
    // ✅ Search method (custom query)
    @Query("SELECT r FROM Resource r WHERE " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.category) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND r.active = true")
    List<Resource> searchResources(@Param("search") String search);
}


   