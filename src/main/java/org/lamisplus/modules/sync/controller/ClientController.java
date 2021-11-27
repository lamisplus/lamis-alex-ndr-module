package org.lamisplus.modules.sync.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.domain.entity.SyncHistory;
import org.lamisplus.modules.sync.domain.entity.Tables;
import org.lamisplus.modules.sync.service.ObjectSerializer;
import org.lamisplus.modules.sync.service.SyncHistoryService;
import org.lamisplus.modules.sync.utility.HttpConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync")
public class ClientController {

    private final SyncHistoryService syncHistoryService;
    private final ObjectSerializer objectSerializer;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${lamis.api.sync}")
    private String serverUrl;

    @GetMapping("/{facilityId}")
    public ResponseEntity<String> sender(@PathVariable("facilityId") Long facilityId) throws Exception  {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            LocalDateTime localDateTime = LocalDateTime.now();
            for (Tables table : Tables.values()) {
                SyncHistory syncHistory = syncHistoryService.getSyncHistory(table.name(), facilityId);
                List<Object> objects = objectSerializer.serialize(table.name(), facilityId, syncHistory.getDateLastSync());
                // Convert object to JSON string and post to the server url
                String pathVariable = table.name().concat("/").concat(Long.toString(facilityId));
                // Convert object to JSON string and post to the server url
                //String response = new HttpConnectionManager().post(mapper.writeValueAsString(objects), "http://localhost:8080/api/sync/" + pathVariable);

                // Convert object to byte array and post to the server url
                String response = new HttpConnectionManager().post(mapper.writeValueAsBytes(objects),
                        serverUrl.concat(pathVariable));
                System.out.println("Response from server: "+response);

                //Save time this table was synced to the server successfully
                syncHistory.setTableName(table.name());
                syncHistory.setOrganisationUnitId(facilityId);
                syncHistory.setDateLastSync(localDateTime);
                syncHistoryService.save(syncHistory);
            }
            return ResponseEntity.ok("Successful");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Fail");
    }
}
