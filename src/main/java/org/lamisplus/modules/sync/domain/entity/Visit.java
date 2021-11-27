package org.lamisplus.modules.sync.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.lamisplus.modules.sync.utility.LocalTimeAttributeConverter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "visit")
public class Visit implements Serializable {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "uuid", updatable = true)
    @JsonIgnore
    private String uuid;

    @Basic
    @Column(name = "date_visit_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dateVisitEnd;

    @Basic
    @Column(name = "date_visit_start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime dateVisitStart;

    @Basic
    @Column(name = "time_visit_start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalDateTime timeVisitStart;

    @Basic
    @Column(name = "time_visit_end", nullable = true)
   @Convert(converter = LocalTimeAttributeConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalDateTime timeVisitEnd;

    @Basic
    @Column(name = "date_next_appointment")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateNextAppointment;

    @Basic
    @Column(name = "patient_id", updatable = false)
    private Long patientId;

    @Basic
    @Column(name = "visit_type_id")
    private Long visitTypeId;

    @Basic
    @Column(name = "type_patient")
    private Integer typePatient;

    @Basic
    @Column(name = "archived")
    @JsonIgnore
    private Integer archived;

    @Basic
    @Column(name = "organisation_unit_id", updatable = false)
    private Long organisationUnitId;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private String createdBy;

    @CreatedDate
    @Column(name = "date_created", nullable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private LocalDateTime dateCreated;

    @LastModifiedBy
    @Column(name = "modified_by")
    @JsonIgnore
    @ToString.Exclude
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "date_modified")
    @JsonIgnore
    @ToString.Exclude
    private LocalDateTime dateModified;

}
