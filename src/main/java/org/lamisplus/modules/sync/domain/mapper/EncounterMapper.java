package org.lamisplus.modules.sync.domain.mapper;

import org.lamisplus.modules.sync.domain.dto.EncounterDTO;
import org.lamisplus.modules.sync.domain.entity.Encounter;
import org.lamisplus.modules.sync.domain.entity.Patient;
import org.lamisplus.modules.sync.domain.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface EncounterMapper {
    @Mappings({
            @Mapping(source="patient.uuid", target="patientUuid"),
            @Mapping(source="visit.uuid", target="visitUuid"),
            @Mapping(source="encounter.uuid", target="uuid"),
            @Mapping(source="encounter.createdBy", target="createdBy"),
            @Mapping(source="encounter.timeCreated", target="dateCreated"),
            @Mapping(source="encounter.modifiedBy", target="modifiedBy"),
            @Mapping(source="encounter.dateModified", target="dateModified"),
            @Mapping(source="encounter.organisationUnitId", target="organisationUnitId"),
            @Mapping(source="encounter.archived", target="archived")

    })
    EncounterDTO toEncounterDTO(Encounter encounter, Patient patient, Visit visit);

    Encounter toEncounter(EncounterDTO encounterDTO);
}
