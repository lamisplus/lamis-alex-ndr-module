package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.FormData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormDataRepository extends JpaRepository<FormData, Long> {

    Optional<FormData> findByUuid(String uuid);
}
