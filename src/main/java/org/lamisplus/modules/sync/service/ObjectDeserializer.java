package org.lamisplus.modules.sync.service;

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
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
            JSONObject object = new JSONObject();
            JSONArray jsonArray = new JSONArray(data);
            switch (table) {
                case "patient" :
                    for (int i = 0; i < jsonArray.length(); i++) {
                        object = jsonArray.optJSONObject(i);
                        PatientDTO patientDTO = objectMapper.readValue(object.toString(), PatientDTO.class);
                        Patient patient = patientMapper.toPatient(patientDTO);
                        patientRepository.findByUuid(patient.getUuid()).ifPresent(value -> patient.setId(value.getId()));
                        System.out.println("patient: "+ patient.toString());
                        patientRepository.save(patient);
                    }
                    break;
                case "visit" :
                    for (int i = 0; i < jsonArray.length(); i++) {
                        object = jsonArray.optJSONObject(i);
                        VisitDTO visitDTO = objectMapper.readValue(object.toString(), VisitDTO.class);
                        Visit visit = visitMapper.toVisit(visitDTO);
                        Optional<Patient> patient = patientRepository.findByUuid(visitDTO.getPatientUuid());
                        if(patient.isPresent()) {
                            visit.setPatientId(patient.get().getId());
                            patientRepository.findByUuid(visitDTO.getPatientUuid()).ifPresent(value -> visit.setPatientId(value.getId()));
                            visitRepository.save(visit);
                        }
                    }
                    break;
                case "encounter" :
                    for (int i = 0; i < jsonArray.length(); i++) {
                        object = jsonArray.optJSONObject(i);
                        EncounterDTO encounterDTO = objectMapper.readValue(object.toString(), EncounterDTO.class);
                        Encounter encounter = encounterMapper.toEncounter(encounterDTO);
                        Optional<Patient> patient = patientRepository.findByUuid(encounterDTO.getPatientUuid());
                        if(patient.isPresent()) {
                            encounter.setPatientId(patient.get().getId());
                            Optional<Visit> visit = visitRepository.findByUuid(encounterDTO.getVisitUuid());
                            if(visit.isPresent()) {
                                encounter.setVisitId(visit.get().getId());
                                encounterRepository.findByUuid(encounterDTO.getUuid()).ifPresent(value -> encounter.setId(value.getId()));
                                encounterRepository.save(encounter);
                            }
                        }
                    }
                    break;
                case "form_data" :
                    for (int i = 0; i < jsonArray.length(); i++) {
                        object = jsonArray.optJSONObject(i);
                        FormDataDTO formDataDTO = objectMapper.readValue(object.toString(), FormDataDTO.class);
                        FormData formData = formDataMapper.toFormData(formDataDTO);
                        Optional<Encounter> encounter = encounterRepository.findByUuid(formDataDTO.getEncounterUuid());
                        if(encounter.isPresent()) {
                            formData.setEncounterId(encounter.get().getId());
                            formDataRepository.findByUuid(formDataDTO.getUuid()).ifPresent(value -> formData.setId(value.getId()));
                            formDataRepository.save(formData);
                        }
                    }
                    break;
                default:
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

    }
}
