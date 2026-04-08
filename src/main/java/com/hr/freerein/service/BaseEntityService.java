package com.hr.freerein.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public abstract class BaseEntityService<T, ID, R extends JpaRepository<T, ID>> {
    
    protected final R repository;
    
    public BaseEntityService(R repository) {
        this.repository = repository;
    }
    
    public List<T> findAll() {
        return repository.findAll();
    }
    
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }
    
    public T save(T entity) {
        return repository.save(entity);
    }
    
    public void deleteById(ID id) {
        repository.deleteById(id);
    }
}