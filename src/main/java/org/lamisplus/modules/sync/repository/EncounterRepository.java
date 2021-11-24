package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {

    Optional<Encounter> findByUuid(String uuid);
}
