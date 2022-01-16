package org.lamisplus.modules.sync.controller;

import com.google.common.hash.Hashing;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.domain.entity.SyncQueue;
import org.lamisplus.modules.sync.service.SyncQueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/sync")
public class ServerController {
    private final SyncQueueService syncQueueService;

    @PostMapping("/{table}/{facilityId}")
    @CircuitBreaker(name = "server2", fallbackMethod = "getReceiverDefault")
    public ResponseEntity<String> receiver(
            @RequestBody byte[] bytes,
            @RequestHeader("Hash-Value") String hash,
            @PathVariable String table,
            @PathVariable Long facilityId) throws Exception {

        SyncQueue syncQueue = syncQueueService.save(bytes, hash, table, facilityId);
        return ResponseEntity.ok(syncQueue.getTableName() + " was save successfully on the server");
    }

    public ResponseEntity<String> getReceiverDefault(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
