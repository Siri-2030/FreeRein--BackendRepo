package com.hr.freerein.repository;


import com.hr.freerein.entity.SafetyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SafetyPlanRepository extends BaseRepository<SafetyPlan, Long> {
    
    List<SafetyPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<SafetyPlan> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);
    
    Optional<SafetyPlan> findByIdAndUserId(Long id, Long userId);
    List<SafetyPlan> findByActiveTrueOrderByCreatedAtDesc();
}