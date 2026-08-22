package com.americobarber.service.impl;

import com.americobarber.config.WhatsAppProperties;
import com.americobarber.entity.Appointment;
import com.americobarber.entity.ServiceEntity;
import com.americobarber.service.WhatsAppService;
import com.americobarber.util.PhoneNumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Envia mensagens de WhatsApp via Evolution API (instância própria conectada por QR code).
 * Texto livre, sem exigência de template aprovado.
 */
@Slf4j
@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private static final String CONFIRMATION_TEXT =
            "Olá %s! Seu agendamento na Américo Barber foi confirmado: %s com %s no dia %s às %s. Nos vemos lá!";
    private static final String REMINDER_TEXT =
            "Olá %s! Lembrete: você tem um horário na Américo Barber hoje às %s para %s com %s. Até já!";

    private final WhatsAppProperties properties;
    private final RestClient restClient;

    public WhatsAppServiceImpl(WhatsAppProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.apiBaseUrl())
                .defaultHeader("apikey", properties.apiKey())
                .build();
    }

    @Override
    @Async("whatsAppTaskExecutor")
    public void sendAppointmentConfirmationAsync(Appointment appointment) {
        List<String> params = buildConfirmationParams(appointment);
        sendMessage(appointment, String.format(CONFIRMATION_TEXT, params.toArray()));
    }

    @Override
    @Async("whatsAppTaskExecutor")
    public void sendAppointmentReminderAsync(Appointment appointment) {
        List<String> params = buildReminderParams(appointment);
        sendMessage(appointment, String.format(REMINDER_TEXT, params.toArray()));
    }

    private boolean isConfigured() {
        return properties.enabled()
                && StringUtils.hasText(properties.apiBaseUrl())
                && StringUtils.hasText(properties.apiKey())
                && StringUtils.hasText(properties.instanceName());
    }

    private void sendMessage(Appointment appointment, String text) {
        if (!isConfigured()) {
            log.warn("WhatsApp não configurado, ignorando envio para agendamento {}", appointment.getId());
            return;
        }

        String phone = PhoneNumberUtil.toE164Brazil(appointment.getClient().getPhone());
        if (phone == null) {
            log.warn("Cliente do agendamento {} não possui telefone válido, ignorando envio de WhatsApp", appointment.getId());
            return;
        }
        String number = phone.substring(1); // Evolution API espera só dígitos, sem "+"

        SendTextRequest request = new SendTextRequest(number, text);
        log.debug("Envio WhatsApp (Evolution API) para agendamento {}: {}", appointment.getId(), request);

        try {
            restClient.post()
                    .uri("/message/sendText/{instance}", properties.instanceName())
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Falha ao enviar WhatsApp para o agendamento {}: {}", appointment.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar WhatsApp para o agendamento {}: {}", appointment.getId(), e.getMessage(), e);
        }
    }

    private List<String> buildConfirmationParams(Appointment appointment) {
        return List.of(
                appointment.getClient().getName(),
                serviceNames(appointment),
                appointment.getBarber().getName(),
                appointment.getDate().format(DATE_FORMAT),
                appointment.getStartTime().format(TIME_FORMAT)
        );
    }

    private List<String> buildReminderParams(Appointment appointment) {
        return List.of(
                appointment.getClient().getName(),
                appointment.getStartTime().format(TIME_FORMAT),
                serviceNames(appointment),
                appointment.getBarber().getName()
        );
    }

    private String serviceNames(Appointment appointment) {
        return appointment.getServices().stream()
                .map(ServiceEntity::getName)
                .collect(Collectors.joining(", "));
    }

    private record SendTextRequest(String number, String text) {
    }
}
