package com.americobarber.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        log.info("Enviando email para {} com assunto '{}'", to, subject);
        try {
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlTemplate = templateEngine.process(templateName, context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, "Américo Barber Club");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlTemplate, true);
            
            mailSender.send(message);
            log.info("Email enviado com sucesso para {}", to);
        } catch (MessagingException e) {
            log.error("Erro ao enviar email para {}", to, e);
            throw new RuntimeException("Falha ao enviar email", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar email para {}", to, e);
            throw new RuntimeException("Erro inesperado ao enviar email", e);
        }
    }
}
