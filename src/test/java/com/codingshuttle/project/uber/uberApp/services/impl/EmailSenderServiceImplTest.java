package com.codingshuttle.project.uber.uberApp.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    private EmailSenderServiceImpl emailSenderService;

    @BeforeEach
    void setUp() {
        emailSenderService = new EmailSenderServiceImpl(javaMailSender);
    }

    @Test
    void sendEmailBuildsMessageForSingleRecipient() {
        emailSenderService.sendEmail("rider@example.com", "Subject", "Body");

        SimpleMailMessage message = capturedMessage();
        assertArrayEquals(new String[]{"rider@example.com"}, message.getTo());
        assertEquals("Subject", message.getSubject());
        assertEquals("Body", message.getText());
    }

    @Test
    void sendEmailBuildsMessageForMultipleRecipients() {
        String[] recipients = {"rider@example.com", "driver@example.com"};

        emailSenderService.sendEmail(recipients, "Subject", "Body");

        assertArrayEquals(recipients, capturedMessage().getTo());
    }

    @Test
    void sendEmailDoesNotPropagateMailSenderFailures() {
        doThrow(new RuntimeException("SMTP unavailable"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailSenderService.sendEmail("rider@example.com", "Subject", "Body"));
    }

    private SimpleMailMessage capturedMessage() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(captor.capture());
        return captor.getValue();
    }
}
