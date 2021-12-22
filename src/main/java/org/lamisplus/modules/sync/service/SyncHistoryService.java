package org.lamisplus.modules.sync.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.sync.domain.entity.OrganisationUnit;
import org.lamisplus.modules.sync.domain.entity.SyncHistory;
import org.lamisplus.modules.sync.repository.OrganisationUnitRepository;
import org.lamisplus.modules.sync.repository.SyncHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SyncHistoryService {

    private final SyncHistoryRepository syncHistoryRepository;
    private final OrganisationUnitRepository organisationUnitRepository;

    public void save(SyncHistory syncHistory) {
        syncHistoryRepository.save(syncHistory);
    }

    public SyncHistory getSyncHistory(String table, Long facilityId){
        Optional<SyncHistory> syncHistory = syncHistoryRepository.findByTableNameAndOrganisationUnitId(table, facilityId);
        return syncHistory.orElseGet(SyncHistory::new);
    }

    public List<SyncHistory> getSyncHistories() {
        List<SyncHistory> syncHistoryList1 = new ArrayList<>();
        List<SyncHistory> syncHistoryList = syncHistoryRepository.findSyncHistories();
        syncHistoryList.forEach(syncHistory -> {
            Optional<OrganisationUnit> organisationUnit = organisationUnitRepository.findById(syncHistory.getOrganisationUnitId());
            if(organisationUnit.isPresent()) {
                syncHistory.setFacilityName(organisationUnit.get().getName());
                syncHistory.setStatus("Processing");
                syncHistoryList1.add(syncHistory);
            }
        });
        return syncHistoryList1;
    }
}
