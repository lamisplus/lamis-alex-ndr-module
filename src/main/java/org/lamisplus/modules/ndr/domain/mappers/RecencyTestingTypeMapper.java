package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.schema.RecencyTestingType;
import org.lamisplus.modules.ndr.domain.schema.YNCodeType;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.base.repository.FormDataRepository;
import org.lamisplus.modules.base.domain.entity.FormData;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecencyTestingTypeMapper {
    private final NdrCodesetService ndrCodesetService;
    private final FormDataRepository formDataRepository;

    public RecencyTestingType map(Encounter encounter) {
        ObjectMapper objectMapper = new ObjectMapper();
        RecencyTestingType recencyTestingType = new RecencyTestingType();

        List<FormData> formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
        FormData formData = null;
        if(!formDataList.isEmpty()) {
            formData = formDataList.get(formDataList.size() - 1);
            JsonNode recency = objectMapper.convertValue(formData, JsonNode.class);

            String consent = StringUtils.trimToEmpty(recency.get("consent").asText());
            recencyTestingType.setConsent(YNCodeType.fromValue(consent));
            recencyTestingType.setRecencyNumber(StringUtils.trimToEmpty(recency.get("participant_number").asText()));
            recencyTestingType.setTestName(StringUtils.trimToEmpty(recency.get("test_name").asText()));
            recencyTestingType.setSampleReferenceNumber(StringUtils.trimToEmpty(recency.get("sample_reference_number").asText()));
            recencyTestingType.setControlLine(YNCodeType.fromValue(StringUtils.trimToEmpty(recency.get("control_line").asText())));
            recencyTestingType.setLongTermLine(YNCodeType.fromValue(StringUtils.trimToEmpty(recency.get("long_term_line").asText())));
            recencyTestingType.setVerificationLine(YNCodeType.fromValue(StringUtils.trimToEmpty(recency.get("verification_line").asText())));
            String pcrLab = StringUtils.trimToEmpty(recency.get("pcr_lab").asText());
            recencyTestingType.setPCRLab(ndrCodesetService.getCode("PCR_LAB", pcrLab));
            recencyTestingType.setFinalRecencyTestResult(recency.get("recency_interpretation").asText());
            recencyTestingType.setRapidRecencyAssay(recency.get("rapid_recency_assay").asText());
            recencyTestingType.setViralLoadClassification(recency.get("viral_load_classification").asText());
            recencyTestingType.setViralLoadConfirmationResult(recency.get("viral_load_test_result").asDouble());
            recencyTestingType.setRecencyInterpretation(recency.get("recency_interpretation").asText());

            try {
                recencyTestingType.setDateSampleCollected((DatatypeFactory.newInstance().newXMLGregorianCalendar(recency.get("date_result_reported").asText())));
                recencyTestingType.setDateSampleSent((DatatypeFactory.newInstance().newXMLGregorianCalendar(recency.get("date_sample_sent").asText())));
                recencyTestingType.setTestDate((DatatypeFactory.newInstance().newXMLGregorianCalendar(recency.get("date_sample_sent").asText())));
                recencyTestingType.setViralLoadConfirmationTestDate((DatatypeFactory.newInstance().newXMLGregorianCalendar(recency.get("viral_load_confirmation_test_date").asText())));
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        return recencyTestingType;
    }
}
