package org.lamisplus.modules.sync.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Biometric {
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private String id;

    @Basic
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Basic
    @Column(name = "organisation_unit_id", updatable = false)
    private Long organisationUnitId;

    private byte[] template;

    private String biometricType;

    private String templateType;

    private LocalDate dateEnrollment;

    private LocalDateTime dateLastModified;

    private Boolean iso;

}