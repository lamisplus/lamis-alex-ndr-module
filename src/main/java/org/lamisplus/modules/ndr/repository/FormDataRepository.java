package org.lamisplus.modules.ndr.repository;

import org.lamisplus.modules.base.domain.entity.FormData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FormDataRepository extends JpaRepository<FormData, Long> {

    List<FormData> findByEncounterId(Long encounterId);

/*
    @Query("select distinct new org.lamisplus.modules.base.domain.entity.FormData(fd.id, fd.data, fd.encounterId, fd.organisationUnitId) from FormData fd where fd.encounterId  = (select e.id from Encounter e  where e.patientId = :patientId and e.formCode = :formCode )")
    List<ManifestDTO> findSampleManifestsDistinct(@Param("dispatched") boolean dispatched);
*/

}
