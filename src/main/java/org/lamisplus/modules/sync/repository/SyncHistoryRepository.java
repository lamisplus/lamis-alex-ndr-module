package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Encounter;
import org.lamisplus.modules.sync.domain.entity.SyncHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SyncHistoryRepository extends JpaRepository<SyncHistory, Long> {

    @Query(nativeQuery = true, value = "select * from sync_history sh order by sh.date_last_sync desc limit 100")
    List<SyncHistory> findSyncHistories();

    Optional<SyncHistory> findByTableNameAndOrganisationUnitId(String tableName, Long organisationUnitId);
}
