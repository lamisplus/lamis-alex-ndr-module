package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, Long> {

    Optional<Encounter> findByUuid(String uuid);

    @Query(value = "SELECT * FROM encounter WHERE uuid is NULL", nativeQuery = true)
    List<Encounter> findNullUuid();
}
