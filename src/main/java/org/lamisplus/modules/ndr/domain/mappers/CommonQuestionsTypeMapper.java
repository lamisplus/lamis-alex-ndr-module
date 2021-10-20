package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.lamisplus.modules.base.repository.FormDataRepository;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.lamisplus.modules.base.domain.entity.FormData;
import org.lamisplus.modules.base.repository.PatientRepository;
import org.lamisplus.modules.ndr.domain.schema.CommonQuestionsType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.ndr.domain.schema.FacilityType;
import org.lamisplus.modules.ndr.service.EncounterService;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.ndr.util.DateUtiil;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommonQuestionsTypeMapper {
    private static final String PATIENT_REGISTRATION_FORM_CODE = "bbc01821-ff3b-463d-842b-b90eab4bdacd";
    private static final String HIV_ENROLMENT_FORM_CODE = "0871d0b9-0fb3-4579-bdec-77d684b0cea6";
    private static final String HIV_STATUS_UPDATE_FORM_CODE = "5210f079-27e9-4d01-a713-a2c400e0926c";
    private static final String CLINIC_VISIT_FORM_CODE = "5c8741de-f722-4e0a-a505-24e039bf4340";

    private final NdrCodesetService ndrCodesetService;
    private final EncounterService encounterService;
    private final PatientRepository patientRepository;
    private final FormDataRepository formDataRepository;
    private final FacilityTypeMapper facilityTypeMapper;

    public CommonQuestionsType map(Encounter encounter) {
        ObjectMapper objectMapper = new ObjectMapper();

        CommonQuestionsType commonQuestionsType = new CommonQuestionsType();

        // Populating patient registration data
        if (encounter.getFormCode().equals(PATIENT_REGISTRATION_FORM_CODE)){
            Optional<Patient> patient = patientRepository.findById(encounter.getPatientId());
            JsonNode patientDetails = objectMapper.convertValue(patient.get().getDetails(), JsonNode.class);
            commonQuestionsType.setHospitalNumber(patient.get().getHospitalNumber());
            LocalDate dob = LocalDate.parse(String.valueOf(patientDetails.get("dob")), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            commonQuestionsType.setPatientAge(Period.between(dob, LocalDate.now()).getYears());
        }

        // Populating HIV enrollment data
        if (encounter.getFormCode().equals(HIV_ENROLMENT_FORM_CODE)){
            Optional<Patient> patient = patientRepository.findById(encounter.getPatientId());
            List<FormData> formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
            if (!formDataList.isEmpty()){
                FormData formData = formDataList.get(formDataList.size() - 1);
                JsonNode hivEnrol = objectMapper.convertValue(formData, JsonNode.class);
                try {
                   JsonNode date = hivEnrol.path("data").path("date_hiv_enrollment");//.asText();
                    System.out.println("Date of HIV enrol " + date);
                    if (!(date instanceof NullNode)){
                        commonQuestionsType.setDiagnosisDate(DateUtiil.getXmlDate(String.valueOf(date)));
                    }
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
                FacilityType facilityType = facilityTypeMapper.map(patient.get().getOrganisationUnitId());
                if (facilityType != null) commonQuestionsType.setDiagnosisFacility(facilityType);
            }
        }

        // Populating clinic visit data
        if (encounter.getFormCode().equals(CLINIC_VISIT_FORM_CODE)){
            Optional<Patient> patient = patientRepository.findById(encounter.getPatientId());
            List<FormData> formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
            if (!formDataList.isEmpty()){
                FormData formData = formDataList.get(formDataList.size() - 1);
                //JsonNode clinicVisit = objectMapper.convertValue(formData, JsonNode.class);
                try {
                    Encounter encounter1;
                    List<Encounter> encounterList = encounterService.getFirstEncounterByFormCode(patient.get(), CLINIC_VISIT_FORM_CODE);
                    if (!encounterList.isEmpty()){
                        encounter1 = encounterList.get(encounterList.size() - 1);
                        String date = String.valueOf(encounter1.getDateEncounter());
                        if (!StringUtils.isBlank(date)){
                            commonQuestionsType.setDateOfFirstReport(DatatypeFactory.newInstance().newXMLGregorianCalendar(date));
                        }
                    }

                    encounterList = encounterService.getLastEncounterByFormCode(patient.get(), CLINIC_VISIT_FORM_CODE);
                    if (!encounterList.isEmpty()){
                        encounter1 = encounterList.get(encounterList.size() - 1);
                        String date = String.valueOf(encounter1.getDateEncounter());
                        if (!StringUtils.isBlank(date)){
                            commonQuestionsType.setDateOfLastReport(DatatypeFactory.newInstance().newXMLGregorianCalendar(date));
                        }
                        List<FormData> formDataList1 = this.formDataRepository.findByEncounterId(encounter.getId());
                        if (!formDataList1.isEmpty()){
                            FormData formData1 = formDataList1.get(formDataList1.size() - 1);
                            JsonNode lastVisit = objectMapper.convertValue(formData1, JsonNode.class);
                            commonQuestionsType.setPatientPregnancyStatusCode(ndrCodesetService.getCode("PREGNANCY_STATUS", lastVisit.path("data").path("pregnancy_status").path("display").asText()));
                            date = String.valueOf(lastVisit.path("data").path("edd"));
                            if (!StringUtils.isBlank(date)){
                                commonQuestionsType.setEstimatedDeliveryDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(date));
                            }
                        }
                    }
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }

        // Populating HIV status update data
        if (encounter.getFormCode().equals(HIV_STATUS_UPDATE_FORM_CODE)){
            List<FormData> formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
            if (!formDataList.isEmpty()){
                FormData formData = formDataList.get(formDataList.size() - 1);
                JsonNode hivStatus = objectMapper.convertValue(formData, JsonNode.class);
                if ((hivStatus.path("data").path("hiv_current_status").path("display").asText()).equals("Died (Confirmed)"))
                    commonQuestionsType.setPatientDieFromThisIllness(true);
            }
        }
        return commonQuestionsType;
    }

}
