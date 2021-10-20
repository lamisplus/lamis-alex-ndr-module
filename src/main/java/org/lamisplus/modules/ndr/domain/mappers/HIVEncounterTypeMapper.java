package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.schema.HIVEncounterType;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.base.repository.FormDataRepository;
import org.lamisplus.modules.base.repository.PatientRepository;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.lamisplus.modules.base.domain.entity.FormData;
import org.lamisplus.modules.ndr.domain.schema.EncountersType;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HIVEncounterTypeMapper {
    private static final int PEDIATRIC_AGE = 14;
    private final NdrCodesetService ndrCodesetService;
    private final PatientRepository patientRepository;
    private final FormDataRepository formDataRepository;

    public HIVEncounterType map(Encounter encounter) {
        ObjectMapper objectMapper = new ObjectMapper();
        HIVEncounterType hivEncounterType = new HIVEncounterType();

        List<FormData> formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
        if(!formDataList.isEmpty()) {
            FormData formData = formDataList.get(formDataList.size() - 1);
            JsonNode clinic = objectMapper.convertValue(formData, JsonNode.class);
            System.out.println("....clinic:  "+clinic);

            try {
                String date = String.valueOf(encounter.getDateEncounter());
                if (!StringUtils.isBlank(date)) hivEncounterType.setVisitDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(date));
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
            hivEncounterType.setVisitID(String.valueOf(encounter.getId()));

            // Getting patient date of birth to compute the age at date of encounter
            Optional<Patient> patient = patientRepository.findById(encounter.getPatientId());
            JsonNode patientDetails = objectMapper.convertValue(patient.get().getDetails(), JsonNode.class);

            LocalDate dob = LocalDate.parse(patientDetails.get("dob").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate dateVisit = encounter.getDateEncounter();
            Period diff = Period.between(dob, dateVisit);
            System.out.println(diff.getYears() + " years " + diff.getMonths() + " months " + diff.getDays() + " days");

            hivEncounterType.setWeight(clinic.path("data").path("body_weight").asInt());
            if(Period.between(dob, dateVisit).getYears() <= PEDIATRIC_AGE) {
                hivEncounterType.setChildHeight(clinic.path("data").path("height").asInt());
            }
            String code = ndrCodesetService.getCode("WHO_STAGE", clinic.path("data").path("clinic_stage").path("display").asText());
            if(code != null) hivEncounterType.setWHOClinicalStage(code);

            code = ndrCodesetService.getCode("FUNCTIONAL_STATUS", clinic.path("data").path("functional_status").path("display").asText());
            if(code != null) hivEncounterType.setFunctionalStatus(code);
            code = ndrCodesetService.getCode("TB_STATUS", clinic.path("data").path("tb_status").path("display").asText());

            if(code != null) hivEncounterType.setTBStatus(code);
        }
        return hivEncounterType;
    }
}
