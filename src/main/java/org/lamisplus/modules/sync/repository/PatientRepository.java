package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUuid(String uuid);

    @Query(value = "SELECT * FROM patient WHERE uuid is NULL", nativeQuery = true)
    List<Patient> findNullUuid();

    @Query(value = "select * from patient where date_modified >=:dateLastSync or date_created >=:dateLastSync", nativeQuery = true)
    List<Patient> getPatientsDueForServerUpload(@Param("dateLastSync") LocalDateTime dateLastSync);

}
