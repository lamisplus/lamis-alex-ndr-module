package org.lamisplus.modules.sync.service;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.lamisplus.modules.sync.repository.SyncQueueRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class SyncQueueService {

    private final SyncQueueRepository syncQueueRepository;

    public  void save(SyncQueue syncQueue) {
        syncQueueRepository.save(syncQueue);
    }

    public SyncQueue save(byte[] bytes, String hash, String table, Long facilityId) throws Exception {
        System.out.println("I am in the server");
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss.ms");
        String folder = ("sync/").concat(Long.toString(facilityId).concat("/")).concat(table).concat("/");
        String fileName = dateFormat.format(date) + "_" + timeFormat.format(date) + ".json";
        File file = new File(folder.concat(fileName));
        FileUtils.writeByteArrayToFile(file, bytes);
        SyncQueue syncQueue = new SyncQueue();
        syncQueue.setFileName(fileName);
        syncQueue.setOrganisationUnitId(facilityId);
        syncQueue.setTableName(table);
        syncQueue.setDateCreated(LocalDateTime.now());

        // Verify the hash value of the byte, if the do not values match set processed to -1
        if (!hash.equals(Hashing.sha256().hashBytes(bytes).toString())) syncQueue.setProcessed(-1);
        else syncQueue.setProcessed(0);

        syncQueue = syncQueueRepository.save(syncQueue);
        return syncQueue;
    }

}
