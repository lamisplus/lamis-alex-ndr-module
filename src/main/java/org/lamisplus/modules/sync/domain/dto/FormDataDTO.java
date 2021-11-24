package org.lamisplus.modules.sync.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class FormDataDTO {

    private String uuid;

    private String encounterUuid;

    private Object data;

    private LocalDateTime dateCreated;

    private String createdBy;

    private LocalDateTime dateModified;

    private String modifiedBy;

    private int archived;
}
