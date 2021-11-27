package org.lamisplus.modules.sync.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.sync.domain.dto.*;
import org.lamisplus.modules.sync.domain.entity.*;
import org.lamisplus.modules.sync.domain.mapper.*;
import org.lamisplus.modules.sync.repository.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectDeserializer {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final EncounterRepository encounterRepository;
    private final FormDataRepository formDataRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientMapper patientMapper;
    private final VisitMapper visitMapper;
    private final EncounterMapper encounterMapper;
    private final FormDataMapper formDataMapper;
    private final AppointmentMapper appointmentMapper;

    public void deserialize(byte[] bytes, String table) {
        String data = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("Data in string: "+data);
        
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
                    List<VisitDTO> visitDTOS = objectMapper.readValue(data, new TypeReference<List<VisitDTO>>() {});
                    visitDTOS.forEach(visitDTO -> {
                        Visit visit = visitMapper.toVisit(visitDTO);
                        patientRepository.findByUuid(visitDTO.getPatientUuid()).ifPresent(value -> visit.setPatientId(value.getId()));
                        visitRepository.findByUuid(visit.getUuid()).ifPresent(value->visit.setId(value.getId()));
                        visitRepository.save(visit);
                    });

                    break;
                case "encounter":
                    List<EncounterDTO> encounterDTOS = objectMapper.readValue(data, new TypeReference<List<EncounterDTO>>() {});
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
                    List<FormDataDTO> formDataDTOS = objectMapper.readValue(data, new TypeReference<List<FormDataDTO>>() {});
                    formDataDTOS.forEach(formDataDTO -> {
                        FormData formData = formDataMapper.toFormData(formDataDTO);
                        encounterRepository.findByUuid(formDataDTO.getEncounterUuid()).ifPresent(value -> formData.setEncounterId(value.getId()));
                        formDataRepository.findByUuid(formData.getUuid()).ifPresent(value->{
                            formData.setId(value.getId());
                        });
                        formDataRepository.save(formData);
                    });
                    break;
                case "appointment":
                    System.out.println("saving appointment");
                    List<AppointmentDTO> appointmentDTOS = objectMapper.readValue(data, new TypeReference<List<AppointmentDTO>>() {});
                    appointmentDTOS.forEach(appointmentDTO -> {
                        Appointment appointment = appointmentMapper.toAppointment(appointmentDTO);
                        visitRepository.findByUuid(appointmentDTO.getVisitUuid())
                                .ifPresent(value -> appointment.setVisitId(value.getId()));
                        patientRepository.findByUuid(appointmentDTO.getPatientUuid())
                                .ifPresent(value -> appointment.setPatientId(value.getId()));
                        encounterRepository.findByUuid(appointment.getUuid())
                                .ifPresent(value-> appointment.setId(value.getId()));
                        appointmentRepository.save(appointment);
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
