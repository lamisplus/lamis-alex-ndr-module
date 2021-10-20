package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.schema.*;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.base.repository.FormDataRepository;
import org.lamisplus.modules.base.domain.entity.FormData;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LaboratoryReportTypeMapper {
    private final NdrCodesetService ndrCodesetService;
    private final FormDataRepository formDataRepository;
    public LaboratoryReportType map(Encounter encounter) {
        ObjectMapper objectMapper = new ObjectMapper();
        LaboratoryReportType laboratoryReportType = new LaboratoryReportType();

        List<FormData> formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
        if (!formDataList.isEmpty()){
            try {
                laboratoryReportType.setVisitDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(String.valueOf(encounter.getDateEncounter())));
                //laboratoryReportType.setVisitDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat("yyyy-MM-dd").format(encounter.getDateEncounter())));
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }

            laboratoryReportType.setVisitID(String.valueOf(encounter.getId()));
            laboratoryReportType.setLaboratoryTestIdentifier("0000001");
            // Iterate through the JsonNode and get these info
            formDataList.forEach(formData -> {
                JsonNode laboratory = objectMapper.convertValue(formData.getData(), JsonNode.class);
                System.out.println(".....laboratory:  " + laboratory);
                Long labtestId = Long.parseLong(String.valueOf(laboratory.get("lab_test_id")));
                String description = String.valueOf(laboratory.get("description"));//.asText();
                String sampleCollectionDate = String.valueOf(laboratory.get("sample_order_date"));
                String dateResultReported = "";
                String resultReported = "";

                JsonNode reportedResults = laboratory.get("reported_result");
                if (reportedResults != null){
                    ArrayNode arrayNode = (ArrayNode) reportedResults;
                    JsonNode lastResult = arrayNode.get(arrayNode.size() - 1); // Get last reported result
                    resultReported = String.valueOf(lastResult.get("result_reported"));
                    dateResultReported = String.valueOf(lastResult.get("date_result_reported"));
                }

                CodedSimpleType cst = ndrCodesetService.getCodedSimpleType("LAB_RESULTED_TEST", description);
                if (cst != null){
                    if (resultReported != null){
                        //Set the NDR code & description for this lab test
                        LaboratoryOrderAndResult laboratoryOrderAndResult = new LaboratoryOrderAndResult();
                        laboratoryOrderAndResult.setLaboratoryResultedTest(cst);
                        try {
                            laboratoryOrderAndResult.setOrderedTestDate((DatatypeFactory.newInstance().newXMLGregorianCalendar(sampleCollectionDate)));
                        } catch (DatatypeConfigurationException e) {
                            e.printStackTrace();
                        }
                        try {
                            laboratoryOrderAndResult.setResultedTestDate((DatatypeFactory.newInstance().newXMLGregorianCalendar(dateResultReported)));
                        } catch (DatatypeConfigurationException e) {
                            e.printStackTrace();
                        }

                        //Set the lab test result values either numeric or text
                        AnswerType answer = new AnswerType();
                        NumericType numeric = new NumericType();

                        if (StringUtils.isNumeric(resultReported)){
                            numeric.setValue1(Float.parseFloat(resultReported));
                            answer.setAnswerNumeric(numeric);
                        } else {
                            if (labtestId == 16){
                                numeric.setValue1(0); //if lab test is a viral load set the value to 0
                                answer.setAnswerNumeric(numeric);
                            } else {
                                answer.setAnswerText(resultReported);
                            }
                        }
                        laboratoryOrderAndResult.setLaboratoryResult(answer);
                        laboratoryReportType.getLaboratoryOrderAndResult().add(laboratoryOrderAndResult);
                    }
                }
            });
        } else
            System.out.println("Encounter no form data (lab): " + encounter.getFormCode() + "  for patient ID: " + encounter.getPatientId());
        return laboratoryReportType;
    }

//    private static void traverse(JsonNode root) {
//        if (root.isObject()){
//            Iterator<String> fieldNames = root.fieldNames();
//            while (fieldNames.hasNext()) {
//                String fieldName = fieldNames.next();
//                JsonNode fieldValue = root.get(fieldName);
//                traverse(fieldValue);
//            }
//        } else if (root.isArray()){
//            ArrayNode arrayNode = (ArrayNode) root;
//            for (int i = 0; i < arrayNode.size(); i++) {
//                JsonNode arrayElement = arrayNode.get(i);
//                traverse(arrayElement);
//            }
//        } else {
//            // JsonNode root represents a single value field - do something with it.
//        }
//    }



}
