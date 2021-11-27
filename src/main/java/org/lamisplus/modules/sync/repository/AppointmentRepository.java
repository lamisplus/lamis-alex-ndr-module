package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByUuid(String uuid);

    @Query(value = "SELECT * FROM appointment WHERE uuid is NULL", nativeQuery = true)
    List<Appointment> findNullUuid();
}
