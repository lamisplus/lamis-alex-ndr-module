package org.lamisplus.modules.ndr.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.lamisplus.modules.base.util.converter.LocalDateConverter;

import javax.persistence.Convert;
import java.time.LocalDate;

@Data
public class FileInfo {
    private String name;
    private String url;
    private Integer numberRecords;

    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateGenerated;
}
