package org.lamisplus.modules.sync.domain.mapper;

import org.lamisplus.modules.sync.domain.dto.EncounterDTO;
import org.lamisplus.modules.sync.domain.entity.Encounter;
import org.lamisplus.modules.sync.domain.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface EncounterMapper {
    @Mappings({
            @Mapping(source="patient.uuid", target="patientUuid"),
    })
    EncounterDTO toEncounterDTO(Encounter encounter, Patient patient);

    Encounter toEncounter(EncounterDTO encounterDTO);
}
