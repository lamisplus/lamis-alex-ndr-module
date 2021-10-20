package org.lamisplus.modules.ndr.repository;

import org.lamisplus.modules.base.domain.entity.Encounter;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EncounterNdrRepository extends JpaRepository<Encounter, Long> {

    @Query(nativeQuery = true, value = "select en from Encounter en where en.patientId = ?1 and en.formCode = ?2 and en.archived = 0 order by en.dateEncounter asc limit 1")
    List<Encounter> findByPatientByPatientIdAndFormCode(Long patientId, String formCode);

    //https://stackoverflow.com/questions/47616482/how-to-limit-result-in-query-used-in-spring-data-repository/47616648
    List<Encounter> findByPatientByPatientIdAndFormCodeAndArchivedOrderByDateEncounter(Patient patient, String formCode, Integer archived, Pageable pageable);

    List<Encounter> findByPatientByPatientIdAndDateModifiedIsAfter(Patient patient, LocalDateTime encounterDate);

    List<Encounter> findByPatientId(Long PatientId);

    Optional<Encounter> findAllByPatientIdAndFormCodeAndArchived(Long patientId, String FormCode, int Archived);

}
