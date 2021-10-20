package org.lamisplus.modules.ndr.repository;

import org.lamisplus.modules.base.domain.entity.OrganisationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface OrganisationUnitNdrRepository extends JpaRepository<OrganisationUnit, Long> {
        List<OrganisationUnit> findAllByArchivedOrderByIdAsc(int unarchived);

    List<OrganisationUnit> findAllByOrganisationUnitLevelId(Long id);
            }
