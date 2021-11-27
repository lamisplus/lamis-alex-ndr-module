package org.lamisplus.modules.sync.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.service.QueueManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync")
public class ServerController {

    private final QueueManager queueManager;

    @PostMapping("/{table}/{facilityId}")
    public ResponseEntity<String> receiver(@RequestBody byte[] bytes, @PathVariable String table, @PathVariable Long facilityId) throws IOException {
        queueManager.queue(bytes, table, facilityId);
        return ResponseEntity.ok("Ok");
    }
}
