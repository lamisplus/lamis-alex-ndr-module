package org.lamisplus.modules.sync.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class VisitDTO {

    Long id;
    private String uuid;

    private String patientUuid;

    //private String organisationUnitUuid;

    private LocalDate dateVisitStart;

    private LocalDate dateVisitEnd;

    private LocalTime timeVisitStart;

    private LocalTime timeVisitEnd;

    private Long visitTypeId;

    private Integer typePatient;

    private LocalDate dateNextAppointment;

    private LocalTime dateCreated;

    private String createdBy;

    private LocalTime dateModified;

    private String modifiedBy;

    private int archived;

}
