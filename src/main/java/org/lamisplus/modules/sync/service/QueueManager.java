package org.lamisplus.modules.sync.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.lamisplus.modules.sync.repository.SyncQueueRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueManager {

    private final ObjectDeserializer objectDeserializer;
    private final SyncQueueRepository syncQueueRepository;

    public void queue(byte[] bytes, String table, Long facilityId) throws IOException {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss.ms");

        String folder = ("sync/").concat(Long.toString(facilityId).concat("/")).concat(table).concat("/");
        String fileName = dateFormat.format(date) + "_" + timeFormat.format(date) + ".json";
        File file = new File(folder.concat(fileName));

        //FileUtils.writeStringToFile(file, data, Charset.defaultCharset());
        FileUtils.writeByteArrayToFile(file, bytes);
        System.out.println("Data in bytes: "+ bytes);

        SyncQueue syncQueue = new SyncQueue();
        syncQueue.setFileName(fileName);
        syncQueue.setOrganisationUnitId(facilityId);
        syncQueue.setTableName(table);
        syncQueue.setDateCreated(LocalDateTime.now());
        syncQueue.setProcessed(0);

        syncQueueRepository.save(syncQueue);

        objectDeserializer.deserialize(bytes, table);
    }

    public void process(byte[] bytes, String table) {
        objectDeserializer.deserialize(bytes, table);
    }
}
