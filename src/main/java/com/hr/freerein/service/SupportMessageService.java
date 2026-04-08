package com.hr.freerein.service;

import com.hr.freerein.entity.SupportMessage;
import com.hr.freerein.repository.SupportMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupportMessageService extends BaseEntityService<SupportMessage, Long, SupportMessageRepository> {  // ✅ Long ID

    public SupportMessageService(SupportMessageRepository repository) {
        super(repository);
    }

    public List<SupportMessage> findBySupportRequestId(Long supportRequestId) {  // ✅ Long parameter
        return repository.findBySupportRequestIdOrderByCreatedAtAsc(supportRequestId);
    }
}