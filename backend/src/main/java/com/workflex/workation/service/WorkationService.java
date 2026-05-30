package com.workflex.workation.service;

import com.workflex.workation.dto.WorkationResponse;
import com.workflex.workation.repository.WorkationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkationService {

    private final WorkationRepository repository;

    public WorkationService(WorkationRepository repository) {
        this.repository = repository;
    }

    /** Returns all workations currently stored in the system. */
    @Transactional(readOnly = true)
    public List<WorkationResponse> findAll() {
        return repository.findAll().stream()
                .map(WorkationResponse::from)
                .toList();
    }
}
