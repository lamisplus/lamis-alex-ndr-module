package org.lamisplus.modules.sync.service;

import lombok.AllArgsConstructor;
import org.lamisplus.modules.sync.domain.entity.OrganisationUnit;
import org.lamisplus.modules.sync.repository.OrganisationUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrganisationUnitService {
    private final OrganisationUnitRepository organisationUnitRepository;

    public List<OrganisationUnit> findOrganisationUnitWithRecords() {
        return organisationUnitRepository.findOrganisationUnitWithRecords();
    }

}
