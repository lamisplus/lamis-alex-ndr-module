package org.lamisplus.modules.sync.service;

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
import org.lamisplus.modules.sync.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectSerializer {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final EncounterRepository encounterRepository;
    private final FormDataRepository formDataRepository;
    private final PatientMapper patientMapper;
    private final EncounterMapper encounterMapper;
    private final FormDataMapper formDataMapper;
    private final VisitMapper visitMapper;
    private  final  ClientRepository clientRepository;

    public List<Object> serialize(String table, long facilityId) {
        List<Object> arrayList = new ArrayList<Object>();
        switch (table) {
            case "patient" :
                List<Patient> patientList = clientRepository.findOrderedByNumberLimitedTo(5);
                patientList.forEach(patient -> {
                    PatientDTO patientDTO = patientMapper.toPatientDTO(patient);
                    arrayList.add(patientDTO);
                });
                break;
            case "visit" :
                List<Visit> visitList = visitRepository.findAll();
                visitList.forEach(visit -> {
                    Patient patient = patientRepository.getById(visit.getPatientId());
                    VisitDTO visitDTO = visitMapper.toVisitDTO(visit, patient);
                    arrayList.add(visitDTO);
                });
                break;
            case "encounter" :
                List<Encounter> encounterList = encounterRepository.findAll();
                encounterList.forEach(encounter -> {
                    Patient patient = patientRepository.getById(encounter.getPatientId());
                    Visit visit = visitRepository.getById(encounter.getVisitId());
                    EncounterDTO encounterDTO = encounterMapper.toEncounterDTO(encounter, patient, visit);
                    arrayList.add(encounterDTO);
                });
                break;
            case "form_data" :
                List<FormData> formDataList = formDataRepository.findAll();
                formDataList.forEach(formData -> {
                    Encounter encounter = encounterRepository.getById(formData.getEncounterId());
                    FormDataDTO formDataDTO = formDataMapper.toFormDataDTO(formData, encounter);
                    arrayList.add(formDataDTO);
                });
                break;
            case "appointment" :
                //retrieve appointment
            default:
                //retrieve biometric
        }
        return arrayList;
    }
}
