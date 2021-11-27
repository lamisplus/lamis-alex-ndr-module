package org.lamisplus.modules.sync.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.sync.domain.entity.Encounter;
import org.lamisplus.modules.sync.domain.entity.FormData;
import org.lamisplus.modules.sync.domain.entity.Patient;
import org.lamisplus.modules.sync.domain.entity.Visit;
import org.lamisplus.modules.sync.repository.EncounterRepository;
import org.lamisplus.modules.sync.repository.FormDataRepository;
import org.lamisplus.modules.sync.repository.PatientRepository;
import org.lamisplus.modules.sync.repository.VisitRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UuidService {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final EncounterRepository encounterRepository;
    private final FormDataRepository formDataRepository;

    @Transactional
    public void addUuid(String table){
        switch (table) {
            case "patient":
                List<Patient> patientList = patientRepository.findNullUuid();
                List<Patient> patientList1 = new ArrayList<>();
                patientList.forEach(patient -> {
                    patient.setUuid(UUID.randomUUID().toString());
                    patientList1.add(patient);
                });
                patientRepository.saveAll(patientList1);
            case "visit":
                List<Visit> visitList = visitRepository.findNullUuid();
                List<Visit> visitList1 = new ArrayList<>();
                visitList.forEach(visit -> {
                    visit.setUuid(UUID.randomUUID().toString());
                    visitList1.add(visit);
                });
                visitRepository.saveAll(visitList1);
            case "encounter":
                List<Encounter> encounterList = encounterRepository.findNullUuid();
                List<Encounter> encounterList1 = new ArrayList<>();
                encounterList.forEach(encounter -> {
                    encounter.setUuid(UUID.randomUUID().toString());
                    encounterList1.add(encounter);
                });
                encounterRepository.saveAll(encounterList1);

            case "form_data":
                List<FormData> formDataList = formDataRepository.findNullUuid();
                List<FormData> formDataList1 = new ArrayList<>();
                formDataList.forEach(formData -> {
                    formData.setUuid(UUID.randomUUID().toString());
                    formDataList1.add(formData);
                });
                formDataRepository.saveAll(formDataList1);

        }
    }
}
