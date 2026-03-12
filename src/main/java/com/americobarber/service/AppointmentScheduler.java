package com.americobarber.service;

import com.americobarber.entity.Appointment;
import com.americobarber.enums.AppointmentStatus;
import com.americobarber.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;

    /**
     * Runs every minute. Automatically finalizes appointments whose endTime has already passed.
     * This makes revenue and stats on the dashboard update without manual intervention.
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void autoFinalizeOverdueAppointments() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Appointment> overdue = appointmentRepository.findOverdueAppointments(today, now);
        if (overdue.isEmpty()) return;

        for (Appointment appointment : overdue) {
            appointment.setStatus(AppointmentStatus.FINALIZADO);
        }
        appointmentRepository.saveAll(overdue);
        log.info("Auto-finalized {} appointment(s).", overdue.size());
    }
}
