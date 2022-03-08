package org.lamisplus.modules.ndr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entity.OrganisationUnit;
import org.lamisplus.modules.ndr.repository.OrganisationUnitNdrRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrganisationUnitNdrService {
    private static final int UNARCHIVED = 0;
    private static final int ARCHIVED = 1;
    private static final Long FIRST_ORG_LEVEL = 1L;
    private final OrganisationUnitNdrRepository organisationUnitNdrRepository;

    public List<OrganisationUnit> findOrganisationUnitWithRecords() {
        return organisationUnitNdrRepository.findOrganisationUnitWithRecords();
    }

}
