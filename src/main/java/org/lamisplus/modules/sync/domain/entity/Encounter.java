package org.lamisplus.modules.sync.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;
import org.lamisplus.modules.sync.utility.LocalDateConverter;
import org.lamisplus.modules.sync.utility.LocalTimeAttributeConverter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@EqualsAndHashCode
@ToString
@Table(name = "encounter")
@Data
public class Encounter implements Serializable {

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "uuid", updatable = true)
    @JsonIgnore
    private String uuid;

    @Basic
    @Column(name = "patient_id", nullable = false)
    private Long patientId;
    @Basic
    @Column(name = "visit_id", nullable = false)
    private Long visitId;

    @Basic
    @Column(name = "form_code", nullable = false)
    private String formCode;
    @Basic
    @Column(name = "program_code", nullable = false)
    private String programCode;

    @Basic
    @Column(name = "date_encounter")
   @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateEncounter;

    @Basic
    @Column(name = "organisation_unit_id", updatable = false)
    private Long organisationUnitId;

    @Basic
    @Column(name = "date_created")
    @JsonIgnore
    @UpdateTimestamp
    private LocalDateTime dateCreated;

    @CreatedBy
    @Basic
    @Column(name = "created_by", updatable = false)
    @JsonIgnore
    private String createdBy;

    @Basic
    @Column(name = "date_modified")
    @JsonIgnore
    @UpdateTimestamp
    private LocalDateTime dateModified;

    @LastModifiedBy
    @Basic
    @Column(name = "modified_by")
    @JsonIgnore
    private String modifiedBy;

    @Basic
    @Column(name = "archived")
    @JsonIgnore
    private Integer archived;

}