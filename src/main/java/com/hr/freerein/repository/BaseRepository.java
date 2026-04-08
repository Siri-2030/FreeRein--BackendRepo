package com.hr.freerein.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    
	
	// ✅ These will work with BaseEntity.active field
    List<T> findByActiveTrue();
    List<T> findByActiveFalse();

    // ✅ Common custom methods for all entities
    Page<T> findByActiveTrue(Pageable pageable);
    
    long countByActiveTrue();
}

