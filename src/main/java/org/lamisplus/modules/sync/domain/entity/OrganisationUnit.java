package org.lamisplus.modules.sync.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "organisation_unit")
public class OrganisationUnit implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "organisation_unit_level_id")
    private Long organisationUnitLevelId;
    @Basic
    @Column(name = "parent_organisation_unit_id")
    private Long parentOrganisationUnitId;
    @Basic
    @Column(name = "archived")
    @JsonIgnore
    private Integer archived = 0;

}
