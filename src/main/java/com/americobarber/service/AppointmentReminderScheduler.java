package com.americobarber.service;

import com.americobarber.entity.Appointment;
import com.americobarber.entity.NotificationSettings;
import com.americobarber.enums.AppointmentStatus;
import com.americobarber.repository.AppointmentRepository;
import com.americobarber.repository.NotificationSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Runs every minute. Sends a WhatsApp reminder to clients whose appointment falls within the
 * admin-configured lead time (reminderMinutesBefore), computed fresh every run since that value
 * can change at any time via the admin settings screen.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final WhatsAppService whatsAppService;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void sendDueReminders() {
        NotificationSettings settings = notificationSettingsRepository.findById(1L)
                .orElse(defaultSettings());

        if (!Boolean.TRUE.equals(settings.getReminderEnabled())) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusMinutes(settings.getReminderMinutesBefore());

        List<Appointment> candidates = appointmentRepository.findDueForReminder(
                AppointmentStatus.AGENDADO, now.toLocalDate(), windowEnd.toLocalDate());

        for (Appointment appointment : candidates) {
            try {
                LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());
                if (appointmentDateTime.isBefore(now) || appointmentDateTime.isAfter(windowEnd)) {
                    continue;
                }
                appointment.setReminderSentAt(Instant.now());
                appointmentRepository.save(appointment);
                whatsAppService.sendAppointmentReminderAsync(appointment);
            } catch (Exception e) {
                log.error("Falha ao processar lembrete do agendamento {}: {}", appointment.getId(), e.getMessage(), e);
            }
        }
    }

    private NotificationSettings defaultSettings() {
        return NotificationSettings.builder()
                .id(1L)
                .confirmationEnabled(true)
                .reminderEnabled(true)
                .reminderMinutesBefore(30)
                .build();
    }
}
