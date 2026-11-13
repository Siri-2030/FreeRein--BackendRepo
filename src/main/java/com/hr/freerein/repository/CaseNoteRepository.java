package com.hr.freerein.repository;

import com.hr.freerein.entity.CaseNote;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseNoteRepository extends BaseRepository<CaseNote, Long> {
    List<CaseNote> findByCaseEntityIdOrderByCreatedAtAsc(Long caseId);
}
