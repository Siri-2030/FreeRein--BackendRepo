package com.hr.freerein.service;

import com.hr.freerein.entity.SupportRequest;
import com.hr.freerein.repository.SupportRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupportRequestService extends BaseEntityService<SupportRequest, Long, SupportRequestRepository> {  // ✅ Long ID

    public SupportRequestService(SupportRequestRepository repository) {
        super(repository);
    }

    public List<SupportRequest> findByUserId(Long userId) {  // ✅ Long parameter
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<SupportRequest> findPending() {
        return repository.findByStatus("PENDING");
    }
}