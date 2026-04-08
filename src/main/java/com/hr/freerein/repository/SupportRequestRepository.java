package com.hr.freerein.repository;

import com.hr.freerein.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupportRequestRepository extends BaseRepository<SupportRequest, Long> {
    
    List<SupportRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<SupportRequest> findByStatus(String status);

    List<SupportRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<SupportRequest> findByTypeAndStatusOrderByCreatedAtDesc(String type, String status);
    
    // ✅ Counsellor assigned requests
    //List<SupportRequest> findByCounsellorIdOrderByCreatedAtDesc(Long counsellorId);
    
    // ✅ Urgent requests
   // @Query("SELECT sr FROM SupportRequest sr WHERE sr.priority = 'HIGH' AND sr.status = 'OPEN' ORDER BY sr.createdAt ASC")
    //List<SupportRequest> findUrgentRequests();
}


