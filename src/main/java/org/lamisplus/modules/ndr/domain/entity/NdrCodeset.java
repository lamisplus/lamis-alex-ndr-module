package org.lamisplus.modules.ndr.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "ndr_codeset")
@Data
@EqualsAndHashCode(of = "id")
public class NdrCodeset {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codeset_group")
    private String codesetGroup;

    @Column(name = "ndr_code")
    private String ndrCode;

    @Column(name = "ndr_description")
    private String ndrDescription;

    @Column(name = "sys_description")
    private String sysDescription;
}
