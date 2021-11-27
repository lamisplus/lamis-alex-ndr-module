package org.lamisplus.modules.sync.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BiometricDTO {

    private String id;

    private Long patientId;

    private Long organisationUnitId;

    private byte[] template;

    private String biometricType;

    private String templateType;

    private LocalDate dateEnrollment;

    private LocalDateTime dateLastModified;

    private Boolean iso;
}
