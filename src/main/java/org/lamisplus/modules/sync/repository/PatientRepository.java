package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository <Patient, Long> {

    Optional<Patient> findByUuid(String uuid);
}
