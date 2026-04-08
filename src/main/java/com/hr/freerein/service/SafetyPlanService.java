package com.hr.freerein.service;

import com.hr.freerein.entity.SafetyPlan;
import com.hr.freerein.repository.SafetyPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SafetyPlanService extends BaseEntityService<SafetyPlan, Long, SafetyPlanRepository> {  // ✅ Long ID

    public SafetyPlanService(SafetyPlanRepository repository) {
        super(repository);
    }

    public List<SafetyPlan> findByUserId(Long userId) {  // ✅ Long parameter
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<SafetyPlan> findActiveByUserId(Long userId) {
        return repository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);  // ✅ Fixed method name
    }
}