package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncQueueRepository extends JpaRepository<SyncQueue, Long> {

    @Query(value = "select * from sync_queue sq where  sq.organisation_unit_id in (select distinct ou.organisation_unit_id from sync_queue ou where ou.processed = 0)", nativeQuery = true)
    List<SyncQueue> getAllSyncQueueByFacilitiesNotProcessed();

    @Query(value = "select * from sync_queue sq where sq.organisation_unit_id =: facilityId and sq.processed = 0 order by sq.date_created desc", nativeQuery = true)
    List<SyncQueue> getAllSyncQueueByFacilityNotProcessed(@Param("facilityId") Long facilityId);
}
