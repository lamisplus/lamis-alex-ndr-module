package org.lamisplus.modules.sync.domain.mapper;

import org.lamisplus.modules.sync.domain.dto.VisitDTO;
import org.lamisplus.modules.sync.domain.entity.Patient;
import org.lamisplus.modules.sync.domain.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    @Mappings({
            @Mapping(source="patient.uuid", target="patientUuid"),
    })
    VisitDTO toVisitDTO(Visit visit, Patient patient);

    Visit toVisit(VisitDTO visitDTO);
}
