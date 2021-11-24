package org.lamisplus.modules.sync.domain.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class PatientDTO {

    private String uuid;

    private String patientNumber;

    private Object details;

    //private String organisationUnitUuid;

    private LocalTime dateCreated;

    private String createdBy;

    private LocalTime dateModified;

    private String modifiedBy;

    private int archived;

}
