package com.hr.freerein.service;

import com.hr.freerein.entity.Case;
import com.hr.freerein.repository.CaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CaseService extends BaseEntityService<Case, Long, CaseRepository> {  // ✅ Long

    public CaseService(CaseRepository repository) {
        super(repository);
    }

    public List<Case> findByUserId(Long userId) {  // ✅ Long
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Case> findByStatus(String status) {
        return repository.findByStatusOrderByCreatedAtDesc(status);
    }
}


