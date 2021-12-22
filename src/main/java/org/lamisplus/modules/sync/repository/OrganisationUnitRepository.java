package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.OrganisationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganisationUnitRepository extends JpaRepository<OrganisationUnit, Long> {
    List<OrganisationUnit> findAllByOrganisationUnitLevelId(Long id);

    @Query(value = "SELECT * FROM organisation_unit org WHERE  org.id IN(SELECT DISTINCT ps.organisation_unit_id FROM patient ps)", nativeQuery = true)
    List<OrganisationUnit> findOrganisationUnitWithRecords();

}
