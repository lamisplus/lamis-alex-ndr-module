package org.lamisplus.modules.sync.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    @CircuitBreaker(name = "Sync", fallbackMethod = "getReceiverDefault")
    public ResponseEntity<String> receiver(
            @RequestBody String data,
            @PathVariable String table,
            @PathVariable Long facilityId) throws IOException {
        queueManager.queue(data, table, facilityId);
        return ResponseEntity.ok("Ok");
    }

    public ResponseEntity<String> getReceiverDefault(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
