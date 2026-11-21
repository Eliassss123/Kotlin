package com.induztek.notifications;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/notificaciones")
public class NotificationController {
    private final JavaMailSender mailSender;
    private final String sender;

    public NotificationController(JavaMailSender mailSender, @Value("${notifications.sender}") String sender) {
        this.mailSender = mailSender;
        this.sender = sender;
    }

    @PostMapping("/recuperacion")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendRecoveryEmail(@Valid @RequestBody RecoveryRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(request.email());
        message.setSubject("Código para recuperar tu contraseña");
        message.setText("Tu código de recuperación es " + request.code()
                + ". Expira en 10 minutos. Si no solicitaste el cambio, ignora este correo.");
        try {
            mailSender.send(message);
        } catch (MailAuthenticationException | MailSendException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Email delivery failed", exception);
        }
    }

    public record RecoveryRequest(@Email @NotBlank String email, @NotBlank String code) {
    }
}
