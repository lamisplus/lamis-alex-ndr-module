package org.lamisplus.modules.sync.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.lamisplus.modules.sync.repository.SyncQueueRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncProcessor {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final Set<Long> runningFacilities = new HashSet<>();
    private final SyncQueueRepository syncQueueRepository;

    public void process() {
        List<SyncQueue> syncQueueList = syncQueueRepository.findDistinctByProcessedEquals(0);
        synchronized (runningFacilities) {
            syncQueueList.forEach(syncQueue -> {
                if (!runningFacilities.contains(syncQueue.getOrganisationUnitId())) {
                    SyncThread syncThread = new SyncThread(syncQueue.getOrganisationUnitId());
                    executorService.execute(syncThread);

                    // add facility to running facility
                    runningFacilities.add(syncQueue.getOrganisationUnitId());
                }
            });
        }
    }
    static class SyncThread implements Runnable {
        private Long facilityId;

        SyncThread(Long facilityId) {
            this.facilityId = facilityId;
        }

        @Override
        public void run() {
            try {
                // process facility files
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            synchronized (runningFacilities) {
                // remove from running facilities
                runningFacilities.remove(facilityId);
            }
        }
    }   ;
}
