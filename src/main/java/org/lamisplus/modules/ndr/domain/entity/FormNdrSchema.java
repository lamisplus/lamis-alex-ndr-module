package org.lamisplus.modules.ndr.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "form_ndr_schema")
@Data
@EqualsAndHashCode(of = "id")
public class FormNdrSchema implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_code")
    private String formCode;

    @Column(name = "ndr_schema")
    private String ndrSchema;
}
