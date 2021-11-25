package org.lamisplus.modules.sync.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "form_data")
public class FormData extends JsonBEntity implements Serializable {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "uuid", updatable = true)
    @JsonIgnore
    private String uuid;

    @Type(type = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    private Object data;

    @Basic
    @Column(name = "encounter_id")
    private Long encounterId;

    @Basic
    @Column(name = "organisation_unit_id", updatable = false)
    @JsonIgnore
    private Long organisationUnitId;
/*

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
*/

}
