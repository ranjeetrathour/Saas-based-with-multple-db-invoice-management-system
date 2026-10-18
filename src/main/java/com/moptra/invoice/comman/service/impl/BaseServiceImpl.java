package com.moptra.invoice.comman.service.impl;


import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@AllArgsConstructor
public abstract class BaseServiceImpl<ENTITY, ID> {

    protected final JpaRepository<ENTITY, ID> repository;

    protected ENTITY save(ENTITY entity) {
        return repository.save(entity);
    }

    protected ENTITY findById(ID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Entity not found: " + id));
    }

    protected List<ENTITY> findAll() {
        return repository.findAll();
    }

    protected void deleteById(ID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Entity not found: " + id);
        }
        repository.deleteById(id);
    }
    
}
