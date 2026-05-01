package com.americobarber.service;

import com.americobarber.entity.Appointment;
import com.americobarber.entity.CancellationPenalty;
import com.americobarber.enums.AppointmentStatus;
import com.americobarber.enums.CancellationPenaltyStatus;
import com.americobarber.repository.AppointmentRepository;
import com.americobarber.repository.CancellationPenaltyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;
    private final CancellationPenaltyRepository cancellationPenaltyRepository;

    /**
     * Runs every minute. Automatically finalizes appointments whose endTime has already passed.
     * Also detects no-shows and creates cancellation penalties.
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void autoFinalizeOverdueAppointments() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Appointment> overdue = appointmentRepository.findOverdueAppointments(today, now);
        if (overdue.isEmpty()) return;

        for (Appointment appointment : overdue) {
            // Mark as NO_SHOW instead of FINALIZADO — the client didn't show up
            appointment.setStatus(AppointmentStatus.NO_SHOW);

            // Create cancellation penalty if not already existing
            if (appointment.getTotalPrice() != null
                    && appointment.getTotalPrice().compareTo(BigDecimal.ZERO) > 0
                    && !cancellationPenaltyRepository.existsByAppointmentId(appointment.getId())) {
                CancellationPenalty penalty = CancellationPenalty.builder()
                        .client(appointment.getClient())
                        .appointment(appointment)
                        .amount(appointment.getTotalPrice())
                        .status(CancellationPenaltyStatus.PENDING)
                        .build();
                cancellationPenaltyRepository.save(penalty);
            }
        }
        appointmentRepository.saveAll(overdue);
        log.info("Detected {} no-show(s) and created penalties.", overdue.size());
    }
}
