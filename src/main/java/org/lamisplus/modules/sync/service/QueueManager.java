package org.lamisplus.modules.sync.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.lamisplus.modules.sync.domain.entity.Tables;
import org.lamisplus.modules.sync.repository.SyncQueueRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueManager {

    private final ObjectDeserializer objectDeserializer;
    private final SyncQueueRepository syncQueueRepository;

    public SyncQueue queue(byte[] bytes, String table, Long facilityId) throws Exception {
        System.out.println("I am in the server");
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss.ms");
        String folder = ("sync/").concat(Long.toString(facilityId).concat("/")).concat(table).concat("/");
        String fileName = dateFormat.format(date) + "_" + timeFormat.format(date) + ".json";
        File file = new File(folder.concat(fileName));

        //FileUtils.writeStringToFile(file, data, Charset.defaultCharset());
        FileUtils.writeByteArrayToFile(file, bytes);
        System.out.println("I am close deserialize the server");
        List<?> deserialize = objectDeserializer.deserialize(bytes, table);
        Object result = deserialize.get(0);
        SyncQueue syncQueue = new SyncQueue();
        if(!result.toString().contains("Nothing was saved on the server")){
            syncQueue.setFileName(fileName);
            syncQueue.setOrganisationUnitId(facilityId);
            syncQueue.setTableName(table);
            syncQueue.setDateCreated(LocalDateTime.now());
            syncQueue.setProcessed(0);
            syncQueue = syncQueueRepository.save(syncQueue);
        }
        if(!syncQueue.getTableName().isEmpty()){
          FileUtils.delete(file);
        }
        return syncQueue;
    }

    public void process(byte[] bytes, String table) throws Exception {
        objectDeserializer.deserialize(bytes, table);
    }
}
