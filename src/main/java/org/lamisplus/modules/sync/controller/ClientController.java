package org.lamisplus.modules.sync.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.domain.entity.OrganisationUnit;
import org.lamisplus.modules.sync.domain.entity.SyncHistory;
import org.lamisplus.modules.sync.domain.entity.Tables;
import org.lamisplus.modules.sync.service.ObjectSerializer;
import org.lamisplus.modules.sync.service.OrganisationUnitService;
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
@RequestMapping("/api/sync")
public class ClientController {
    private static final String UPLOAD = "upload";
    private final ObjectSerializer objectSerializer;
    private final ObjectMapper mapper = new ObjectMapper();
    private final SyncHistoryService syncHistoryService;
    private final OrganisationUnitService organisationUnitService;

    @Value("${server.url}")
    private String SERVER_URL;

    @GetMapping("/{facilityId}")
    @CircuitBreaker(name = UPLOAD, fallbackMethod = "getDefaultMessage")
    public ResponseEntity<String> sender(@PathVariable("facilityId") Long facilityId) throws Exception {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        System.out.println("table values: => "+ Arrays.toString(Tables.values()));
        for (Tables table : Tables.values()) {
            SyncHistory syncHistory = syncHistoryService.getSyncHistory(table.name(), facilityId);
            LocalDateTime dateLastSync = syncHistory.getDateLastSync();
            log.info("last date sync 1 {}", dateLastSync);

            List<?> serializeTableRecords = objectSerializer.serialize(table, facilityId, dateLastSync);
            if(!serializeTableRecords.isEmpty()){
            Object serializeObject = serializeTableRecords.get(0);
            log.info("serialize first  object  {} ", serializeObject.toString());
            if (!serializeObject.toString().contains("No table records was retrieved for server sync")) {
                String pathVariable = table.name().concat("/").concat(Long.toString(facilityId));
                String url = SERVER_URL.concat(pathVariable);
                String response = new HttpConnectionManager().post(mapper.writeValueAsBytes(serializeTableRecords), url);
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

    @GetMapping("/facilities")
    public ResponseEntity<List<OrganisationUnit>> getAllOrganizationUnit() {
        return ResponseEntity.ok(organisationUnitService.findOrganisationUnitWithRecords());
    }

    @GetMapping("/synchistory")
    public ResponseEntity<List<SyncHistory>> getSyncHistories() {
        return ResponseEntity.ok(syncHistoryService.getSyncHistories());
    }

    public ResponseEntity<String> getDefaultMessage(Exception exception) {
        String message = exception.getMessage();
        if (message.contains("Failed to connect")) {
            message = "server is down kindly try again later";
        }
        return ResponseEntity.internalServerError().body(message);
    }


}
