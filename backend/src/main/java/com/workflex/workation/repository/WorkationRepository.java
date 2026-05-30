package com.workflex.workation.repository;

import com.workflex.workation.domain.Workation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkationRepository extends JpaRepository<Workation, String> {
}
