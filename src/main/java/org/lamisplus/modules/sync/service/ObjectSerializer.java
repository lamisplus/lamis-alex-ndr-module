package org.lamisplus.modules.sync.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.domain.entity.*;
import org.lamisplus.modules.sync.domain.mapper.*;
import org.lamisplus.modules.sync.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectSerializer {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final EncounterRepository encounterRepository;
    private final FormDataRepository formDataRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientMapper patientMapper;
    private final EncounterMapper encounterMapper;
    private final FormDataMapper formDataMapper;
    private final VisitMapper visitMapper;
    private final AppointmentMapper appointmentMapper;
    private final ClientRepository clientRepository;
    private final UuidService uuidService;

    public List<?> serialize(Tables table, long facilityId, LocalDateTime dateLastSync) {

        if (table.name().equals("patient")) {
            log.info(" Retrieving records from  {} ", table.name());
            List<Patient> patientList = new LinkedList<>();
            if (dateLastSync == null) {
                patientList = patientRepository.findAll();
                System.out.println(patientList);
            } else {
                patientList = patientRepository.getPatientsDueForServerUpload(dateLastSync);
            }
            return patientList.stream()
                    .filter(patient -> patient.getOrganisationUnitId().equals(facilityId))
                    .map(patientMapper::toPatientDTO)
                    .collect(Collectors.toList());
        }


        if (table.name().equals("visit")) {
            log.info(" Retrieving records from  {} ", table.name());
            List<Visit> visitList = new LinkedList<>();
            if (dateLastSync == null) {
                visitList = visitRepository.findAll();
            } else {
                visitList = visitRepository.getVisitsDueForServerUpload(dateLastSync);
            }
            return visitList.stream()
                    .filter(visit -> visit.getOrganisationUnitId().equals(facilityId))
                    .map(visit -> {
                        Patient patient = patientRepository.getById(visit.getPatientId());
                        return visitMapper.toVisitDTO(visit, patient);
                    }).collect(Collectors.toList());


        }
        if (table.name().equals("encounter")) {
            log.info(" Retrieving records from  {} ", table.name());
            List<Encounter> encounterList = new LinkedList<>();
            if (dateLastSync == null) {
                encounterList = encounterRepository.findAll();
            } else {
                encounterList = encounterRepository.getEncountersDueForServerUpload(dateLastSync);
            }
            return encounterList.stream()
                    .filter(encounter -> encounter.getOrganisationUnitId().equals(facilityId))
                    .map(encounter -> {
                        Patient patient = patientRepository.getById(encounter.getPatientId());
                        Visit visit = visitRepository.getById(encounter.getVisitId());
                        return encounterMapper.toEncounterDTO(encounter, patient, visit);
                    }).collect(Collectors.toList());
        }
        if (table.name().equals("form_data")) {
            log.info(" Retrieving records from  {} ", table.name());
            List<FormData> formDataList = new LinkedList<>();
//            if (dateLastSync == null) {
                formDataList = formDataRepository.findAll();
//            } else {
//                formDataList = formDataRepository.getFormDataDueForServerUpload(dateLastSync);
//            }
            return formDataList.stream()
                    .filter(formData -> formData.getOrganisationUnitId().equals(facilityId))
                    .map(formData -> {
                        Encounter encounter = encounterRepository.getById(formData.getEncounterId());
                        return formDataMapper.toFormDataDTO(formData, encounter);
                    }).collect(Collectors.toList());

        }

        if (table.name().equals("appointment")) {
            log.info(" Retrieving records from  {} ", table.name());
            List<Appointment> appointmentList = new LinkedList<>();
            if (dateLastSync == null) {
                appointmentList = appointmentRepository.findAll();
            } else {
                appointmentList = appointmentRepository.getAppointmentsDueForServerUpload(dateLastSync);
            }
            return appointmentList.stream()
                    .filter(appointment -> appointment.getOrganisationUnitId().equals(facilityId))
                    .map(appointment -> {
                        Patient patient = patientRepository.getById(appointment.getPatientId());
                        Visit visit = visitRepository.getById(appointment.getVisitId());
                        return appointmentMapper.toAppointmentDTO(appointment, patient, visit);
                    }).collect(Collectors.toList());

        }
        List<String> msg = new LinkedList<>();
        msg.add("No table records was retrieved for server sync");
        return msg;

    }
}
