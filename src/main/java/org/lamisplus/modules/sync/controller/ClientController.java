package org.lamisplus.modules.sync.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.domain.entity.SyncHistory;
import org.lamisplus.modules.sync.domain.entity.Tables;
import org.lamisplus.modules.sync.service.ObjectSerializer;
import org.lamisplus.modules.sync.service.SyncHistoryService;
import org.lamisplus.modules.sync.utility.HttpConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/sync")
public class ClientController {
    private static final String UPLOAD = "upload";
    private final ObjectSerializer objectSerializer;
    private final ObjectMapper mapper = new ObjectMapper();
    private final SyncHistoryService syncHistoryService;
    @Value("${server.url}")
    private String SERVER_URL;


    @GetMapping("/{facilityId}")
    @CircuitBreaker(name = "service2", fallbackMethod = "getDefaultMessage")
    @Retry(name = "retryService2", fallbackMethod = "retryFallback")
    public ResponseEntity<String> sender(@PathVariable("facilityId") Long facilityId) throws Exception {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        System.out.println("table values: => " + Arrays.toString(Tables.values()));
        for (Tables table : Tables.values()) {
            SyncHistory syncHistory = syncHistoryService.getSyncHistory(table.name(), facilityId);
            LocalDateTime dateLastSync = syncHistory.getDateLastSync();
            log.info("last date sync 1 {}", dateLastSync);
            List<?> serializeTableRecords = objectSerializer.serialize(table, facilityId, dateLastSync);
            if (!serializeTableRecords.isEmpty()) {
                Object serializeObjet = serializeTableRecords.get(0);
//                log.info("serialize first  object  {} ", serializeObjet.toString());
                log.info("object size:  {} ", serializeTableRecords.size());
                if (!serializeObjet.toString().contains("No table records was retrieved for server sync")) {
                    String pathVariable = table.name().concat("/").concat(Long.toString(facilityId));
                    System.out.println("path: "+ pathVariable);
                    String url = SERVER_URL.concat(pathVariable);
                    byte[] bytes = mapper.writeValueAsBytes(serializeTableRecords);
//                    System.out.println("output: "+bytes);
                    String response = new HttpConnectionManager().post(bytes, url);
                    System.out.println("==>: "+ response);
                    log.info("Done : {}", response);
                    syncHistory.setTableName(table.name());
                    syncHistory.setOrganisationUnitId(facilityId);
                    syncHistory.setDateLastSync(LocalDateTime.now());
                    syncHistoryService.save(syncHistory);
                }
            }
        }
        return ResponseEntity.ok("Successful");

    }

    public ResponseEntity<String> getDefaultMessage(Exception exception) {
        String message = exception.getMessage();
        if (message.contains("Failed to connect")) {
            message = "server is down kindly try again later";
        }
        return ResponseEntity.internalServerError().body(message);

    }

    public ResponseEntity<String> retryFallback(Exception exception) {
        String message = exception.getMessage();
        if (message.contains("Failed to connect")) {
            message = "server is down kindly try again later inside retry!!!";
        }
        return ResponseEntity.internalServerError().body(message);
    }
}
