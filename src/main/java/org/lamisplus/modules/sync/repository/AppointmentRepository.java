package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Appointment;
import org.lamisplus.modules.sync.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByUuid(String uuid);

    @Query(value = "SELECT * FROM appointment WHERE uuid is NULL", nativeQuery = true)
    List<Appointment> findNullUuid();

    @Query(value = "select * from appointment where " +
            "date_modified >=:dateLastSync or" +
            " date_created >=:dateLastSync",
            nativeQuery = true)
    List<Appointment> getAppointmentsDueForServerUpload(@Param("dateLastSync") LocalDateTime dateLastSync);
}
