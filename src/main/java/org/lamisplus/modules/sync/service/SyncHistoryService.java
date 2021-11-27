package org.lamisplus.modules.sync.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.sync.domain.entity.SyncHistory;
import org.lamisplus.modules.sync.repository.SyncHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SyncHistoryService {

    private final SyncHistoryRepository syncHistoryRepository;

    public void save(SyncHistory syncHistory) {
        syncHistoryRepository.save(syncHistory);
    }

    public SyncHistory getSyncHistory(String table, Long facilityId){
        Optional<SyncHistory> syncHistory = syncHistoryRepository.findByTableNameAndOrganisationUnitId(table, facilityId);
        return syncHistory.orElseGet(SyncHistory::new);
    }
}
