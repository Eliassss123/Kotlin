package com.induztek.notifications;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;

class NotificationControllerTest {
    @Test
    void sendsRecoveryCodeByEmail() {
        JavaMailSender sender = org.mockito.Mockito.mock(JavaMailSender.class);
        NotificationController controller = new NotificationController(sender, "test@example.com");

        controller.sendRecoveryEmail(new NotificationController.RecoveryRequest("user@example.com", "124578"));

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(message.capture());
        assertEquals("user@example.com", message.getValue().getTo()[0]);
        org.junit.jupiter.api.Assertions.assertTrue(message.getValue().getText().contains("124578"));
    }

    @Test
    void reportsMailDeliveryFailure() {
        JavaMailSender sender = org.mockito.Mockito.mock(JavaMailSender.class);
        doThrow(new MailSendException("offline")).when(sender).send(any(SimpleMailMessage.class));
        NotificationController controller = new NotificationController(sender, "test@example.com");

        assertThrows(ResponseStatusException.class,
                () -> controller.sendRecoveryEmail(new NotificationController.RecoveryRequest("user@example.com", "124578")));
    }
}
