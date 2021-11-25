package org.lamisplus.modules.sync.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.service.ObjectSerializer;
import org.lamisplus.modules.sync.utility.HttpConnectionManager;
import org.lamisplus.modules.sync.utility.UuidService;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync")
public class ClientController {
    public enum Tables {
        patient
        //visit,
        //encounter,
        //form_data,
        //appointment,
        //biometric
    };

    private final UuidService uuidService;
    private final ObjectSerializer objectSerializer;
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/{facilityId}")
    public ResponseEntity<String> sender(@PathVariable("facilityId") Long facilityId) throws JSONException {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        uuidService.addUuid();

        try {
            for (Tables table : Tables.values()) {
                List<Object> objects = objectSerializer.serialize(table.name(), facilityId);
                // Convert object to JSON string and post to the server url
                String pathVariable = table.name().concat("/").concat(Long.toString(facilityId));
                String response = new HttpConnectionManager().post(mapper.writeValueAsString(objects),
                        "http://localhost:8080/api/sync/" + pathVariable);
                System.out.println("Response from server: "+response);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Successful");
    }
}
