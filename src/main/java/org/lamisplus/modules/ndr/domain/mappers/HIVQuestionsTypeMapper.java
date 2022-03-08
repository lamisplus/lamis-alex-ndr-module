package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.domain.entity.Regimen;
import org.lamisplus.modules.base.repository.PatientRepository;
import org.lamisplus.modules.base.repository.RegimenRepository;
import org.lamisplus.modules.ndr.domain.entity.NdrCodeset;
import org.lamisplus.modules.ndr.domain.schema.CodedSimpleType;
import org.lamisplus.modules.ndr.domain.schema.FacilityType;
import org.lamisplus.modules.ndr.domain.schema.HIVQuestionsType;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.base.repository.EncounterRepository;
import org.lamisplus.modules.base.repository.FormDataRepository;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.lamisplus.modules.base.domain.entity.FormData;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HIVQuestionsTypeMapper {
    private static final String HIV_ENROLMENT_FORM_CODE = "0871d0b9-0fb3-4579-bdec-77d684b0cea6";
    private static final String ART_COMMENCEMENT_FORM_CODE = "0a8b31d2-9397-42f8-9300-688b62c75571";
    private static final String HIV_STATUS_UPDATE_FORM_CODE = "5210f079-27e9-4d01-a713-a2c400e0926c";

    private static final int PEDIATRIC_AGE = 14;
    private final NdrCodesetService ndrCodesetService;
    private final FacilityTypeMapper facilityTypeMapper;

    private final PatientRepository patientRepository;
    private final FormDataRepository formDataRepository;
    private final RegimenRepository regimenRepository;

    public HIVQuestionsType map(Encounter encounter) {
        ObjectMapper objectMapper = new ObjectMapper();

        HIVQuestionsType hivQuestionsType = new HIVQuestionsType();
        FormData formData;
        List<FormData> formDataList;
        String data, date;
        Integer numeric;
        CodedSimpleType codedSimpleType;

        // Populating HIV enrollment data
        if(encounter.getFormCode().equals(HIV_ENROLMENT_FORM_CODE)) {
            formDataList  = (formDataRepository.findByEncounterId(encounter.getId()));
            if(!formDataList.isEmpty()) {
                formData = formDataList.get(formDataList.size() - 1);
                JsonNode hivEnrol = objectMapper.convertValue(formData, JsonNode.class);
                System.out.println("......hiv enrol: "+hivEnrol);

                data = hivEnrol.path("data").path("care_entry_point").asText();
                if (!StringUtils.isEmpty(data)) hivQuestionsType.setCareEntryPoint(data);
                try {
                    date = hivEnrol.path("data").path("date_hiv_enrollment").asText();
                    if (!StringUtils.isEmpty(date)) hivQuestionsType.setEnrolledInHIVCareDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(date));
                    date = hivEnrol.path("data").path("date_confirmed_hiv").asText();
                    hivQuestionsType.setFirstConfirmedHIVTestDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(date));
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }

        //Populating ART start data
        if(encounter.getFormCode().equals(ART_COMMENCEMENT_FORM_CODE)) {
            formDataList = formDataRepository.findByEncounterId(encounter.getId());
            if(!formDataList.isEmpty()) {
                formData = formDataList.get(formDataList.size() - 1);
                JsonNode artStart = objectMapper.convertValue(formData, JsonNode.class);
                System.out.println("......art start: "+artStart);

                try {
                    date = artStart.path("data").path("date_art_start").asText();
                    if (!StringUtils.isEmpty(date)) hivQuestionsType.setARTStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(date));
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }

                // Getting patient date of birth to compute the age at date of encounter
                Optional<Patient> patient = patientRepository.findById(encounter.getPatientId());
                JsonNode patientDetails = objectMapper.convertValue(patient.get().getDetails(), JsonNode.class);
                LocalDate dob = LocalDate.parse(patientDetails.get("dob").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate dateVisit = formData.getEncounterByEncounterId().getDateEncounter();
                //LocalDate dateVisit = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(formData.getEncounterByEncounterId().getDateEncounter()));

                numeric = artStart.path("data").path("body_weight").asInt();
                if (numeric != null) hivQuestionsType.setWeightAtARTStart(numeric);
                if(Period.between(dob, dateVisit).getYears() <= PEDIATRIC_AGE) {
                    numeric = artStart.path("data").path("height").asInt();
                    if (numeric != null) hivQuestionsType.setChildHeightAtARTStart(numeric);
                }

                Optional<Regimen> regimen = regimenRepository.findById(artStart.path("data").path("regimen_id").asLong());
                codedSimpleType = ndrCodesetService.getCodedSimpleType("ARV_REGIMEN", regimen.get().getName());
                if (codedSimpleType != null) hivQuestionsType.setFirstARTRegimen(codedSimpleType);

                data = artStart.path("data").path("cd4").asText();
                if(!StringUtils.isEmpty(data)) hivQuestionsType.setCD4AtStartOfART(data);
                data = ndrCodesetService.getCode("WHO_STAGE", artStart.path("data").path("clinic_stage").path("display").asText());
                if(!StringUtils.isEmpty(data)) hivQuestionsType.setWHOClinicalStageARTStart(data);

                data =ndrCodesetService.getCode("FUNCTIONAL_STATUS", artStart.path("data").path("functional_status").path("display").asText());
                if(!StringUtils.isEmpty(data)) hivQuestionsType.setFunctionalStatusStartART(data);

                data = ndrCodesetService.getCode("TB_STATUS", artStart.path("data").path("tb_status").path("display").asText());
                if(!StringUtils.isEmpty(data)) hivQuestionsType.setInitialTBStatus(data);
            }
        }

        //Populating HIV status update
        if(encounter.getFormCode().equals(HIV_STATUS_UPDATE_FORM_CODE)) {
           formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
            if(!formDataList.isEmpty()) {
                formData = formDataList.get(formDataList.size() - 1);
                JsonNode hivStatus = objectMapper.convertValue(formData, JsonNode.class);
                String currentStatus = hivStatus.path("data").path("hiv_current_status").path("display").asText();
                if(currentStatus.equals("Died (Confirmed)")) {
                    hivQuestionsType.setPatientHasDied(true);
                    hivQuestionsType.setStatusAtDeath("ART");
                    try {
                        hivQuestionsType.setDeathDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(hivStatus.path("data").path("date_hiv_current_status").asText()));        } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                }

                if(currentStatus.equals("ART Transfer In") || currentStatus.equals("Pre-ART Transfer In")) {
                    hivQuestionsType.setPatientTransferredIn(true);
                    try {
                        hivQuestionsType.setTransferredInDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(hivStatus.path("data").path("date_hiv_current_status").asText()));        } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                    //hivQuestionsType.setTransferredInFrom(facilityTypeMapper.map(encounter.getOrganisationUnitId()));
                    //hivQuestionsType.setTransferredInFromPatId("");
                }

                if(currentStatus.equals("ART Transfer Out") || currentStatus.equals("Pre-ART Transfer Out")) {
                    hivQuestionsType.setPatientTransferredOut(true);
                    if(currentStatus.equals("ART Transfer Out"))
                        hivQuestionsType.setTransferredOutStatus("ART");
                    else hivQuestionsType.setTransferredOutStatus("Pre-ART");
                    try {
                        hivQuestionsType.setTransferredOutDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(hivStatus.path("data").path("date_hiv_current_status").asText()));        } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                    hivQuestionsType.setFacilityReferredTo(facilityTypeMapper.map(encounter.getOrganisationUnitId()));
                }

                if(currentStatus.equals("Stopped Treatment")) {
                    hivQuestionsType.setStoppedTreatment(true);
                    try {
                        hivQuestionsType.setDateStoppedTreatment(DatatypeFactory.newInstance().newXMLGregorianCalendar(hivStatus.path("data").path("date_hiv_current_status").asText()));        } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return hivQuestionsType;
    }
}
