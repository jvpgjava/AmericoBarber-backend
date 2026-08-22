package com.americobarber.service;

import com.americobarber.entity.Appointment;

public interface WhatsAppService {

    void sendAppointmentConfirmationAsync(Appointment appointment);

    void sendAppointmentReminderAsync(Appointment appointment);
}
