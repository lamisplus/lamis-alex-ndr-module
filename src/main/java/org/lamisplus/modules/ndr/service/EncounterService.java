package org.lamisplus.modules.ndr.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.lamisplus.modules.ndr.repository.EncounterNdrRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EncounterService {
    private  final EncounterNdrRepository encounterRepository;

    public List<Encounter> getFirstEncounterByFormCode(Patient patient, String formCode) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "dateEncounter"));
        return encounterRepository.findByPatientByPatientIdAndFormCodeAndArchivedOrderByDateEncounter(patient, formCode, 0, pageable);
    }

    public List<Encounter> getLastEncounterByFormCode(Patient patient, String formCode) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "dateEncounter"));
        return encounterRepository.findByPatientByPatientIdAndFormCodeAndArchivedOrderByDateEncounter(patient, formCode, 0, pageable);
    }

}
