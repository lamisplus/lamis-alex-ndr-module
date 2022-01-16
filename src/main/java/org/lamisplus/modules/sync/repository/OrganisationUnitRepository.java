package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.OrganisationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganisationUnitRepository extends JpaRepository<OrganisationUnit, Long> {
    List<OrganisationUnit> findAllByOrganisationUnitLevelId(Long id);

    @Query(value = "select * from organisation_unit org where  org.id in (select distinct ps.organisation_unit_id from patient ps)", nativeQuery = true)
    List<OrganisationUnit> findOrganisationUnitWithRecords();

}
