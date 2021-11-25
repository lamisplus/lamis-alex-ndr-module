package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Patient;
import org.lamisplus.modules.sync.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository <Patient, Long> {

    Optional<Patient> findByUuid(String uuid);

    @Query(value = "SELECT * FROM patient WHERE uuid is NULL", nativeQuery = true)
    List<Patient> findNullUuid();
}
