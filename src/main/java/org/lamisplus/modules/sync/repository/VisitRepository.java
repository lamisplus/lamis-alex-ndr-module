package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    Optional<Visit> findByUuid(String uuid);
}
