package org.lamisplus.modules.sync.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {QueueManager.class})
@ExtendWith(SpringExtension.class)
class QueueManagerTest {
    @MockBean
    private ObjectDeserializer objectDeserializer;


    @Autowired
    private QueueManager queueManager;


/*
    @Test
    void testProcess() {
        doNothing().when(this.objectDeserializer).deserialize((String) any(), (String) any());
        this.queueManager.process("Data", "Table");
        verify(this.objectDeserializer).deserialize((String) any(), (String) any());
    }
*/
}

