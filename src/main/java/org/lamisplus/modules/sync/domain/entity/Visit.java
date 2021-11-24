package org.lamisplus.modules.sync.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "visit")
public class Visit extends Audit implements Serializable {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "uuid", updatable = false)
    @JsonIgnore
    private String uuid;

    @Basic
    @Column(name = "date_visit_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateVisitEnd;

    @Basic
    @Column(name = "date_visit_start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateVisitStart;

    @Basic
    @Column(name = "time_visit_start")
  //  @Convert(converter = LocalTimeAttributeConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalTime timeVisitStart;

    @Basic
    @Column(name = "time_visit_end", nullable = true)
  //  @Convert(converter = LocalTimeAttributeConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalTime timeVisitEnd;

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
}
