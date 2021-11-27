package org.lamisplus.modules.sync.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ContextConfiguration(classes = {ObjectDeserializer.class})
@ExtendWith(SpringExtension.class)
class ObjectDeserializerTest {
    @MockBean
    private EncounterMapper encounterMapper;

    @MockBean
    private EncounterRepository encounterRepository;

    @MockBean
    private FormDataMapper formDataMapper;

    @MockBean
    private FormDataRepository formDataRepository;

    @Autowired
    private ObjectDeserializer objectDeserializer;

    @MockBean
    private PatientMapper patientMapper;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private VisitMapper visitMapper;

    @MockBean
    private VisitRepository visitRepository;

/*
    @Test
    void testDeserialize() {
        // TODO: This test is incomplete.
        //   Reason: R004 No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by deserialize(String, String)
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        this.objectDeserializer.deserialize("Data", "Table");
    }

    @Test
    void testDeserialize2() {
        // TODO: This test is incomplete.
        //   Reason: R004 No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by deserialize(String, String)
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        this.objectDeserializer.deserialize("encounter", "Table");
    }

    @Test
    void testDeserialize3() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("Data", "encounter"));
    }

    @Test
    void testDeserialize4() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("Data", "form_data"));
    }

    @Test
    void testDeserialize5() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("Data", "patient"));
    }

    @Test
    void testDeserialize6() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("encounter", "encounter"));
    }

    @Test
    void testDeserialize7() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("Data", "visit"));
    }

    @Test
    void testDeserialize8() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("42", "encounter"));
    }

    @Test
    void testDeserialize9() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("", "encounter"));
    }

    @Test
    void testDeserialize10() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("42", "form_data"));
    }

    @Test
    void testDeserialize11() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("42", "patient"));
    }

    @Test
    void testDeserialize12() {
        assertThrows(RuntimeException.class, () -> this.objectDeserializer.deserialize("42", "visit"));
    }
*/
}

