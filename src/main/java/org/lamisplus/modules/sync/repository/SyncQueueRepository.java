package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncQueueRepository extends JpaRepository<SyncQueue, Long> {
    List<SyncQueue> getAllByProcessed(Integer processed);
}
