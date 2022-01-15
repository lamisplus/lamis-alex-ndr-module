package org.lamisplus.modules.sync.service;

import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.lamisplus.modules.sync.repository.SyncQueueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueManager {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final Set<Long> runningFacilities = new HashSet<>();
    private final SyncQueueRepository syncQueueRepository;
    private final ObjectDeserializer objectDeserializer;

    @Scheduled(fixedDelay = 300000)
    public void queue() {
        List<SyncQueue> syncQueues = syncQueueRepository.getAllSyncQueueByFacilitiesNotProcessed();
        synchronized (runningFacilities) {
            syncQueues.forEach(syncQueue -> {
                if (!runningFacilities.contains(syncQueue.getOrganisationUnitId())) {
                    SyncThread syncThread = new SyncThread(syncQueue.getOrganisationUnitId());
                    executorService.execute(syncThread);
                    // add facility to running facilities
                    runningFacilities.add(syncQueue.getOrganisationUnitId());
                }
            });
        }
    }

     class SyncThread implements Runnable {
        private Long facilityId;

        SyncThread(Long facilityId) {
            this.facilityId = facilityId;
        }

        @Override
        public void run() {
            try {
                // process facility files
                List<SyncQueue> syncQueues = syncQueueRepository.getAllSyncQueueByFacilityNotProcessed(facilityId);
                log.info("available file for processing are : {}", syncQueues.size());
                syncQueues.forEach(syncQueue -> {
                    String folder = ("sync/").concat(Long.toString(syncQueue.getOrganisationUnitId())
                            .concat("/")).concat(syncQueue.getTableName()).concat("/");
                    File file = FileUtils.getFile(folder, syncQueue.getFileName());
                    try {
                        InputStream targetStream = new FileInputStream(file);
                        byte[] bytes = ByteStreams.toByteArray(Objects.requireNonNull(targetStream));
                        List<?> list = objectDeserializer.deserialize(bytes, syncQueue.getTableName());
                        if(!list.isEmpty()){
                            syncQueue.setProcessed(1);
                            syncQueueRepository.save(syncQueue);
                            FileUtils.deleteDirectory(file);
                            log.info("deleting file : {}", file.getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            synchronized (runningFacilities) {
                // remove from running facilities
                runningFacilities.remove(facilityId);
            }
        }
    }
    ;
}
