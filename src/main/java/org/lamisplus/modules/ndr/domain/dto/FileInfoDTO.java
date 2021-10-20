package org.lamisplus.modules.ndr.domain.dto;

import lombok.Data;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

@Data
public class FileInfoDTO {
    List<FileInfo> fileInfos = new ArrayList<>();

}
