package org.lamisplus.modules.sync.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lamisplus.modules.sync.domain.dto.EncounterDTO;
import org.lamisplus.modules.sync.domain.entity.Encounter;
import org.lamisplus.modules.sync.domain.entity.Patient;
import org.lamisplus.modules.sync.domain.entity.Visit;
import org.lamisplus.modules.sync.domain.mapper.EncounterMapper;
import org.lamisplus.modules.sync.domain.mapper.FormDataMapper;
import org.lamisplus.modules.sync.domain.mapper.PatientMapper;
import org.lamisplus.modules.sync.domain.mapper.VisitMapper;
import org.lamisplus.modules.sync.repository.ClientRepository;
import org.lamisplus.modules.sync.repository.EncounterRepository;
import org.lamisplus.modules.sync.repository.FormDataRepository;
import org.lamisplus.modules.sync.repository.PatientRepository;
import org.lamisplus.modules.sync.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ObjectSerializer.class})
@ExtendWith(SpringExtension.class)
class ObjectSerializerTest {
    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private EncounterMapper encounterMapper;

    @MockBean
    private EncounterRepository encounterRepository;

    @MockBean
    private FormDataMapper formDataMapper;

    @MockBean
    private FormDataRepository formDataRepository;

    @Autowired
    private ObjectSerializer objectSerializer;

    @MockBean
    private PatientMapper patientMapper;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private UuidService uuidService;

    @MockBean
    private VisitMapper visitMapper;

    @MockBean
    private VisitRepository visitRepository;
/*
    @Test
    void testSerialize() {
        assertTrue(this.objectSerializer.serialize("Table", 123L).isEmpty());
        assertTrue(this.objectSerializer.serialize("appointment", 123L).isEmpty());
        assertTrue(this.objectSerializer.serialize("Table", 123L).isEmpty());
        assertTrue(this.objectSerializer.serialize("appointment", 123L).isEmpty());
    }

    @Test
    void testSerialize2() {
        doNothing().when(this.uuidService).addUuid((String) any());
        when(this.encounterRepository.findAll()).thenReturn(new ArrayList<Encounter>());
        assertTrue(this.objectSerializer.serialize("encounter", 123L).isEmpty());
        verify(this.uuidService).addUuid((String) any());
        verify(this.encounterRepository).findAll();
    }

    @Test
    void testSerialize3() {
        Visit visit = new Visit();
        visit.setPatientId(123L);
        visit.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        visit.setOrganisationUnitId(123L);
        visit.setTimeVisitStart(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setTypePatient(1);
        visit.setArchived(1);
        visit.setDateVisitEnd(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setTimeVisitEnd(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setDateNextAppointment(LocalDate.ofEpochDay(1L));
        visit.setId(123L);
        visit.setDateVisitStart(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        visit.setVisitTypeId(123L);
        visit.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        visit.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setDateCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        when(this.visitRepository.getById((Long) any())).thenReturn(visit);
        doNothing().when(this.uuidService).addUuid((String) any());

        Patient patient = new Patient();
        patient.setPatientNumber("42");
        patient.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        patient.setId(123L);
        patient.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        patient.setDetails("Details");
        patient.setOrganisationUnitId(123L);
        patient.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        patient.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        patient.setArchived(1);
        patient.setDateCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        when(this.patientRepository.getById((Long) any())).thenReturn(patient);

        Encounter encounter = new Encounter();
        encounter.setPatientId(123L);
        encounter.setVisitId(123L);
        encounter.setTimeCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        encounter.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounter.setId(123L);
        encounter.setProgramCode("encounter");
        encounter.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        encounter.setOrganisationUnitId(123L);
        encounter.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        encounter.setFormCode("encounter");
        encounter.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        encounter.setArchived(0);
        encounter.setDateEncounter(LocalDateTime.of(1, 1, 1, 1, 1));

        ArrayList<Encounter> encounterList = new ArrayList<Encounter>();
        encounterList.add(encounter);
        when(this.encounterRepository.findAll()).thenReturn(encounterList);
        when(this.encounterMapper.toEncounterDTO((Encounter) any(), (Patient) any(), (Visit) any()))
                .thenReturn(new EncounterDTO());
        assertEquals(1, this.objectSerializer.serialize("encounter", 123L).size());
        verify(this.visitRepository).getById((Long) any());
        verify(this.uuidService).addUuid((String) any());
        verify(this.patientRepository).getById((Long) any());
        verify(this.encounterRepository).findAll();
        verify(this.encounterMapper).toEncounterDTO((Encounter) any(), (Patient) any(), (Visit) any());
    }

    @Test
    void testSerialize4() {
        doNothing().when(this.uuidService).addUuid((String) any());
        when(this.encounterRepository.findAll()).thenReturn(new ArrayList<Encounter>());
        assertTrue(this.objectSerializer.serialize("encounter", 123L).isEmpty());
        verify(this.uuidService).addUuid((String) any());
        verify(this.encounterRepository).findAll();
    }

    @Test
    void testSerialize5() {
        Visit visit = new Visit();
        visit.setPatientId(123L);
        visit.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        visit.setOrganisationUnitId(123L);
        visit.setTimeVisitStart(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setTypePatient(1);
        visit.setArchived(1);
        visit.setDateVisitEnd(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setTimeVisitEnd(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setDateNextAppointment(LocalDate.ofEpochDay(1L));
        visit.setId(123L);
        visit.setDateVisitStart(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        visit.setVisitTypeId(123L);
        visit.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        visit.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        visit.setDateCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        when(this.visitRepository.getById((Long) any())).thenReturn(visit);
        doNothing().when(this.uuidService).addUuid((String) any());

        Patient patient = new Patient();
        patient.setPatientNumber("42");
        patient.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        patient.setId(123L);
        patient.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        patient.setDetails("Details");
        patient.setOrganisationUnitId(123L);
        patient.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        patient.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        patient.setArchived(1);
        patient.setDateCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        when(this.patientRepository.getById((Long) any())).thenReturn(patient);

        Encounter encounter = new Encounter();
        encounter.setPatientId(123L);
        encounter.setVisitId(123L);
        encounter.setTimeCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        encounter.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounter.setId(123L);
        encounter.setProgramCode("encounter");
        encounter.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        encounter.setOrganisationUnitId(123L);
        encounter.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        encounter.setFormCode("encounter");
        encounter.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        encounter.setArchived(0);
        encounter.setDateEncounter(LocalDateTime.of(1, 1, 1, 1, 1));

        ArrayList<Encounter> encounterList = new ArrayList<Encounter>();
        encounterList.add(encounter);
        when(this.encounterRepository.findAll()).thenReturn(encounterList);
        when(this.encounterMapper.toEncounterDTO((Encounter) any(), (Patient) any(), (Visit) any()))
                .thenReturn(new EncounterDTO());
        assertEquals(1, this.objectSerializer.serialize("encounter", 123L).size());
        verify(this.visitRepository).getById((Long) any());
        verify(this.uuidService).addUuid((String) any());
        verify(this.patientRepository).getById((Long) any());
        verify(this.encounterRepository).findAll();
        verify(this.encounterMapper).toEncounterDTO((Encounter) any(), (Patient) any(), (Visit) any());
    }
 */
}

