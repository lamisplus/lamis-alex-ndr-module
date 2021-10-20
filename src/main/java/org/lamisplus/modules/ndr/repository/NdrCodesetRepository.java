package org.lamisplus.modules.ndr.repository;

import org.lamisplus.modules.ndr.domain.entity.NdrCodeset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NdrCodesetRepository extends JpaRepository<NdrCodeset, Long> {
    Optional<NdrCodeset> findByCodesetGroupAndSysDescription(String codeSetGroup, String description);
}
