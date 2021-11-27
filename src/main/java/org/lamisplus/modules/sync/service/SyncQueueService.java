package org.lamisplus.modules.sync.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.lamisplus.modules.sync.repository.SyncQueueRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncQueueService {

    private final SyncQueueRepository syncQueueRepository;

    public void save(SyncQueue syncQueue) {
        syncQueueRepository.save(syncQueue);
    }
}
