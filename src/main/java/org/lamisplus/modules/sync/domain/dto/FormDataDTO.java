package org.lamisplus.modules.sync.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Basic;
import javax.persistence.Column;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class FormDataDTO {

    private String uuid;

    private String encounterUuid;

    private Object data;


    private Long organisationUnitId;

//    private LocalDateTime dateCreated;
//
//    private String createdBy;
//
//    private LocalDateTime dateModified;
//
//    private String modifiedBy;

//    private int archived;
}
