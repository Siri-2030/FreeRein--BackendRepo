package com.hr.freerein.repository;

import com.hr.freerein.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends BaseRepository<Case, Long> {
    
    // ✅ ADD THESE EXACT METHODS
    List<Case> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Case> findByStatusOrderByCreatedAtDesc(String status);
    
    // ✅ Additional useful methods
    //List<Case> findByCounsellorIdOrderByCreatedAtDesc(Long counsellorId);
    //List<Case> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
    List<Case> findByActiveTrueOrderByCreatedAtDesc();
}

