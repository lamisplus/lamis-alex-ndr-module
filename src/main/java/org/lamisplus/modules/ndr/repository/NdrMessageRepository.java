package org.lamisplus.modules.ndr.repository;

import org.lamisplus.modules.ndr.domain.entity.NdrMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NdrMessageRepository extends JpaRepository<NdrMessage, Long> {
    Optional<NdrMessage> findByPatientId(Long patientId);
    List<NdrMessage> findByOrganisationUnitIdAndMarshalled(Long organisationUnitId, boolean marshalled);

}
