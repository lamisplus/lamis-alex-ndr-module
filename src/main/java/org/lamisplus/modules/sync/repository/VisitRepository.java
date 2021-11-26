package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.FormData;
import org.lamisplus.modules.sync.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    Optional<Visit> findByUuid(String uuid);

    @Query(value = "SELECT * FROM visit WHERE uuid is NULL", nativeQuery = true)
    List<Visit> findNullUuid();
}
