package com.hr.freerein.repository;

import com.hr.freerein.entity.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupportMessageRepository extends BaseRepository<SupportMessage, Long> {
    
    List<SupportMessage> findBySupportRequestIdOrderByCreatedAtAsc(Long supportRequestId);
    
    @Query("SELECT sm FROM SupportMessage sm " +
           "WHERE sm.supportRequest.user.id = :userId " +
           "ORDER BY sm.createdAt ASC")
    List<SupportMessage> findByUserIdOrderByCreatedAtAsc(@Param("userId") Long userId);
    
    // ✅ Recent messages
    List<SupportMessage> findTop10BySupportRequestIdOrderByCreatedAtDesc(Long supportRequestId);
}