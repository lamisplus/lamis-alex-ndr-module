package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.SyncHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyncHistoryRepository extends JpaRepository<SyncHistory, Long> {

    Optional<SyncHistory> findByTableNameAndOrganisationUnitId(String tableName, Long organisationUnitId);
}
