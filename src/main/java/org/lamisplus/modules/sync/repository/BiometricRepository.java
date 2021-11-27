package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Biometric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BiometricRepository extends JpaRepository<Biometric, Long> {
    Optional<Biometric> findById(String uuid);

    @Query(value = "SELECT * FROM biometric WHERE id is NULL", nativeQuery = true)
    List<Biometric> findNullUuid();
}
