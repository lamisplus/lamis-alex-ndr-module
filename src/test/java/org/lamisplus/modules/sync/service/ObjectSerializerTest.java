package org.lamisplus.modules.sync.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lamisplus.modules.sync.domain.dto.EncounterDTO;
import org.lamisplus.modules.sync.domain.dto.PatientDTO;
import org.lamisplus.modules.sync.domain.entity.Encounter;
import org.lamisplus.modules.sync.domain.entity.Patient;
import org.lamisplus.modules.sync.domain.mapper.EncounterMapper;
import org.lamisplus.modules.sync.domain.mapper.FormDataMapper;
import org.lamisplus.modules.sync.domain.mapper.PatientMapper;
import org.lamisplus.modules.sync.domain.mapper.VisitMapper;
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
    private EncounterMapper encounterMapper;

    @MockBean
    private EncounterRepository encounterRepository;

    @MockBean
    private FormDataMapper formDataMapper;

    @MockBean
    private FormDataRepository formDataRepository;

    @Autowired
    private ObjectSerializer undertest;

    @MockBean
    private PatientMapper patientMapper;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private VisitMapper visitMapper;

    @MockBean
    private VisitRepository visitRepository;

    @Test
    void testSerialize() {
        assertTrue(this.undertest.serialize("Table", 123L).isEmpty());
        assertTrue(this.undertest.serialize("appointment", 123L).isEmpty());
    }

    @Test
    void testSerialize2() {
        when(this.encounterRepository.findAll()).thenReturn(new ArrayList<Encounter>());
        assertTrue(this.undertest.serialize("encounter", 123L).isEmpty());
        verify(this.encounterRepository).findAll();
    }

    @Test
    void testSerialize3() {
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
        encounter.setDateEncounter(LocalDate.ofEpochDay(1L));
        encounter.setPatientId(123L);
        encounter.setVisitId(123L);
        encounter.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounter.setId(123L);
        encounter.setProgramCode("encounter");
        encounter.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        encounter.setOrganisationUnitId(123L);
        encounter.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        encounter.setFormCode("encounter");
        encounter.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        encounter.setArchived(0);
        encounter.setTimeCreated(LocalTime.of(1, 1));

        ArrayList<Encounter> encounterList = new ArrayList<Encounter>();
        encounterList.add(encounter);
        when(this.encounterRepository.findAll()).thenReturn(encounterList);

        EncounterDTO encounterDTO = new EncounterDTO();
        encounterDTO.setPatientUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounterDTO.setArchived(1);
        encounterDTO.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounterDTO.setProgramCode("Program Code");
        encounterDTO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        encounterDTO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        encounterDTO.setFormCode("Form Code");
        encounterDTO.setVisitUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounterDTO.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        encounterDTO.setDateCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        when(this.encounterMapper.toEncounterDTO((Encounter) any(), (Patient) any())).thenReturn(encounterDTO);
        assertEquals(1, this.undertest.serialize("encounter", 123L).size());
        verify(this.patientRepository).getById((Long) any());
        verify(this.encounterRepository).findAll();
        verify(this.encounterMapper).toEncounterDTO((Encounter) any(), (Patient) any());
    }

    @Test
    void testSerialize4() {
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
        encounter.setDateEncounter(LocalDate.ofEpochDay(1L));
        encounter.setPatientId(123L);
        encounter.setVisitId(123L);
        encounter.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounter.setId(123L);
        encounter.setProgramCode("encounter");
        encounter.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        encounter.setOrganisationUnitId(123L);
        encounter.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        encounter.setFormCode("encounter");
        encounter.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        encounter.setArchived(0);
        encounter.setTimeCreated(LocalTime.of(1, 1));

        Encounter encounter1 = new Encounter();
        encounter1.setDateEncounter(LocalDate.ofEpochDay(1L));
        encounter1.setPatientId(123L);
        encounter1.setVisitId(123L);
        encounter1.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounter1.setId(123L);
        encounter1.setProgramCode("encounter");
        encounter1.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        encounter1.setOrganisationUnitId(123L);
        encounter1.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        encounter1.setFormCode("encounter");
        encounter1.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        encounter1.setArchived(0);
        encounter1.setTimeCreated(LocalTime.of(1, 1));

        ArrayList<Encounter> encounterList = new ArrayList<Encounter>();
        encounterList.add(encounter1);
        encounterList.add(encounter);
        when(this.encounterRepository.findAll()).thenReturn(encounterList);

        EncounterDTO encounterDTO = new EncounterDTO();
        encounterDTO.setPatientUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounterDTO.setArchived(1);
        encounterDTO.setUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounterDTO.setProgramCode("Program Code");
        encounterDTO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        encounterDTO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        encounterDTO.setFormCode("Form Code");
        encounterDTO.setVisitUuid("01234567-89AB-CDEF-FEDC-BA9876543210");
        encounterDTO.setDateModified(LocalDateTime.of(1, 1, 1, 1, 1));
        encounterDTO.setDateCreated(LocalDateTime.of(1, 1, 1, 1, 1));
        when(this.encounterMapper.toEncounterDTO((Encounter) any(), (Patient) any())).thenReturn(encounterDTO);
        assertEquals(2, this.undertest.serialize("encounter", 123L).size());
        verify(this.patientRepository, atLeast(1)).getById((Long) any());
        verify(this.encounterRepository).findAll();
        verify(this.encounterMapper, atLeast(1)).toEncounterDTO((Encounter) any(), (Patient) any());
    }

    @Test
    void shouldTestPatientTable() {
        //given
        String table = "patient";
        Long facilityId = 9003l;
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
        List<Patient> patientList = new ArrayList<>();
        patientList.add(patient);
        PatientDTO patientDTO = PatientDTO.builder()
                .archived(1)
                .patientNumber("testing")
                .build();

        when(this.patientMapper.toPatientDTO(patient)).thenReturn(patientDTO);
        when(this.patientRepository.findAll()).thenReturn(patientList);
        //when
        List<Object> serialize = undertest.serialize(table, facilityId);
        serialize.forEach(e -> {
            System.out.println("Seri=> : " + e.toString());
        });
        verify(patientRepository).findAll();

        //then
    }


}

