package org.lamisplus.modules.sync.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.sync.domain.dto.EncounterDTO;
import org.lamisplus.modules.sync.domain.dto.FormDataDTO;
import org.lamisplus.modules.sync.domain.dto.PatientDTO;
import org.lamisplus.modules.sync.domain.dto.VisitDTO;
import org.lamisplus.modules.sync.domain.entity.Encounter;
import org.lamisplus.modules.sync.domain.entity.FormData;
import org.lamisplus.modules.sync.domain.entity.Patient;
import org.lamisplus.modules.sync.domain.entity.Visit;
import org.lamisplus.modules.sync.domain.mapper.EncounterMapper;
import org.lamisplus.modules.sync.domain.mapper.FormDataMapper;
import org.lamisplus.modules.sync.domain.mapper.PatientMapper;
import org.lamisplus.modules.sync.domain.mapper.VisitMapper;
import org.lamisplus.modules.sync.repository.EncounterRepository;
import org.lamisplus.modules.sync.repository.FormDataRepository;
import org.lamisplus.modules.sync.repository.PatientRepository;
import org.lamisplus.modules.sync.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectDeserializer {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final EncounterRepository encounterRepository;
    private final FormDataRepository formDataRepository;
    private final PatientMapper patientMapper;
    private final VisitMapper visitMapper;
    private final EncounterMapper encounterMapper;
    private final FormDataMapper formDataMapper;

    public void deserialize(String data, String table) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            switch (table) {
                case "patient":
                    List<PatientDTO> patientDTOS = objectMapper.readValue(data, new TypeReference<List<PatientDTO>>() {});
                    patientDTOS.forEach(patientDTO -> {
                        Patient patient = patientMapper.toPatient(patientDTO);
                        patientRepository.findByUuid(patient.getUuid()).ifPresent(value -> patient.setId(value.getId()));
                        patientRepository.save(patient);
                    });
                    break;
                case "visit":
                    System.out.println("saving visit records on the server");
                    List<VisitDTO> visitDTOS = objectMapper.readValue(data, new TypeReference<List<VisitDTO>>() {
                    });
                    visitDTOS.forEach(visitDTO -> {
                        Visit visit = visitMapper.toVisit(visitDTO);
                        patientRepository.findByUuid(visitDTO.getPatientUuid()).ifPresent(value -> visit.setPatientId(value.getId()));
                        visitRepository.findByUuid(visit.getUuid()).ifPresent(value->visit.setId(value.getId()));
                        visitRepository.save(visit);
                    });

                    break;
                case "encounter":
                    List<EncounterDTO> encounterDTOS = objectMapper.readValue(data, new TypeReference<List<EncounterDTO>>() {
                    });
                    encounterDTOS.forEach(encounterDTO -> {
                        Encounter encounter = encounterMapper.toEncounter(encounterDTO);
                        visitRepository.findByUuid(encounterDTO.getVisitUuid())
                                .ifPresent(value -> encounter.setVisitId(value.getId()));
                        patientRepository.findByUuid(encounterDTO.getPatientUuid())
                                .ifPresent(value -> encounter.setPatientId(value.getId()));
                        encounterRepository.findByUuid(encounter.getUuid())
                                .ifPresent(value-> encounter.setId(value.getId()));
                        encounterRepository.save(encounter);
                    });

                    break;
                case "form_data":
                    System.out.println("saving form data");
                    List<FormDataDTO> formDataDTOS = objectMapper.readValue(data, new TypeReference<List<FormDataDTO>>() {
                    });
                    formDataDTOS.forEach(formDataDTO -> {
                        FormData formData = formDataMapper.toFormData(formDataDTO);
                        encounterRepository.findByUuid(formDataDTO.getEncounterUuid()).ifPresent(value -> formData.setEncounterId(value.getId()));
                        formDataRepository.findByUuid(formData.getUuid()).ifPresent(value->{
                            formData.setId(value.getId());
                        });
                        formDataRepository.save(formData);
                    });
                    break;
                default:
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

    }
}
