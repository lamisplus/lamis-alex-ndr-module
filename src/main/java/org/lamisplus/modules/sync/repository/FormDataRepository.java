package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.FormData;
import org.lamisplus.modules.sync.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormDataRepository extends JpaRepository<FormData, Long> {

    Optional<FormData> findByUuid(String uuid);

    @Query(value = "SELECT * FROM form_data WHERE uuid is NULL", nativeQuery = true)
    List<FormData> findNullUuid();
}
