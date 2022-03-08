package org.lamisplus.modules.ndr.repository;

import org.lamisplus.modules.base.domain.entity.OrganisationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganisationUnitNdrRepository extends JpaRepository<OrganisationUnit, Long> {
    List<OrganisationUnit> findAllByArchivedOrderByIdAsc(int unarchived);

    List<OrganisationUnit> findAllByOrganisationUnitLevelId(Long id);

    @Query(value = "select * from organisation_unit org where  org.id in(select distinct ps.organisation_unit_id from patient ps)", nativeQuery = true)
    List<OrganisationUnit> findOrganisationUnitWithRecords();

}
