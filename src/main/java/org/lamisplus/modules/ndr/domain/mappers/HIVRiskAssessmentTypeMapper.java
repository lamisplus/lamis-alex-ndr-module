package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.ndr.domain.schema.HIVRiskAssessmentType;
import org.lamisplus.modules.base.domain.entity.FormData;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.lamisplus.modules.base.repository.FormDataRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HIVRiskAssessmentTypeMapper {
    private final FormDataRepository formDataRepository;

    public HIVRiskAssessmentType map(Encounter encounter) {
        ObjectMapper objectMapper = new ObjectMapper();
        HIVRiskAssessmentType hivRiskAssessmentType = new HIVRiskAssessmentType();

        List<FormData> formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
        if(!formDataList.isEmpty()) {
            FormData formData = formDataList.get(formDataList.size() - 1);
            JsonNode assessment = objectMapper.convertValue(formData, JsonNode.class);
        }

        return hivRiskAssessmentType;
    }
}
