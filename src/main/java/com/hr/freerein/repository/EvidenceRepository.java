package com.hr.freerein.repository;

import com.hr.freerein.entity.Evidence;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenceRepository extends BaseRepository<Evidence, Long> {
    List<Evidence> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);
    Optional<Evidence> findByIdAndUserId(Long id, Long userId);
}
