package org.lamisplus.modules.sync.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lamisplus.modules.sync.domain.dto.PatientDTO;
import org.lamisplus.modules.sync.domain.dto.VisitDTO;
import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.lamisplus.modules.sync.repository.SyncQueueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        FileUtils.writeByteArrayToFile(file, bytes);
        SyncQueue syncQueue = new SyncQueue();
        syncQueue.setFileName(fileName);
        syncQueue.setOrganisationUnitId(facilityId);
        syncQueue.setTableName(table);
        syncQueue.setDateCreated(LocalDateTime.now());
        syncQueue.setProcessed(0);
        syncQueue = syncQueueRepository.save(syncQueue);
        return syncQueue;
    }


    @Scheduled(fixedDelay = 300000)
    public void process() throws Exception {
        List<SyncQueue> filesNotProcessed = syncQueueRepository.getAllByProcessed(0);
        log.info("available file for processing are : {}", filesNotProcessed.size());
        filesNotProcessed
                .forEach(syncQueue -> {
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
    }
}
