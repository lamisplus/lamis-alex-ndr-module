package org.lamisplus.modules.sync.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class EncounterDTO {

    private String uuid;

    private String patientUuid;

    private String visitUuid;

    //private String organisationUnitUuid;

    private String formCode;

    private String programCode;

    private LocalDateTime dateCreated;

    private String createdBy;

    private LocalDateTime dateModified;

    private String modifiedBy;

    private int archived;

}
