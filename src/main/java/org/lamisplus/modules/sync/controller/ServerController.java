package org.lamisplus.modules.sync.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.lamisplus.modules.sync.domain.entity.Tables;
import org.lamisplus.modules.sync.service.QueueManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/sync")
public class ServerController {
    private final QueueManager queueManager;

    @PostMapping("/{table}/{facilityId}")
    @CircuitBreaker(name = "server2", fallbackMethod = "getReceiverDefault")
    public ResponseEntity<String> receiver(
            @RequestBody byte[] data,
            @PathVariable String table,
            @PathVariable Long facilityId) throws Exception {
        SyncQueue syncQueue = queueManager.queue(data, table, facilityId);
        return ResponseEntity.ok(syncQueue.getTableName() + " was save successfully on the server");
    }

    public ResponseEntity<String> getReceiverDefault(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
