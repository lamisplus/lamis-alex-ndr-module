package org.lamisplus.modules.sync.domain.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class EncounterDTO {

    private String uuid;

    private String patientUuid;

    private String visitUuid;

    //private String organisationUnitUuid;

    private String formCode;

    private String programCode;

    private LocalTime dateCreated;

    private String createdBy;

    private LocalTime dateModified;

    private String modifiedBy;

    private int archived;

}
