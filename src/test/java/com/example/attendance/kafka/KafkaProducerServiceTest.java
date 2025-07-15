package com.example.attendance.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class KafkaProducerServiceTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        kafkaProducerService = new KafkaProducerService();

        // Use reflection or setter to inject the mock if the field is private and no constructor exists
        var field = KafkaProducerService.class.getDeclaredFields()[0]; // Assumes kafkaTemplate is first field
        field.setAccessible(true);
        try {
            field.set(kafkaProducerService, kafkaTemplate);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSendMessage() {
        // given
        String testMessage = "Hello Kafka";

        // when
        kafkaProducerService.sendMessage(testMessage);

        // then
        verify(kafkaTemplate, times(1)).send("employee-events", testMessage);
    }
}
