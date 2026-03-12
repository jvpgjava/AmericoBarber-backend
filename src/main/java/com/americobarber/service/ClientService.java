package com.americobarber.service;

import com.americobarber.dto.request.AppointmentRequest;
import com.americobarber.dto.request.RescheduleRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;

import java.time.LocalDate;
import java.util.List;

public interface ClientService {

    UserResponse getProfile(Long clientId);

    List<AppointmentResponse> myAppointments(Long clientId);

    List<AppointmentResponse> myHistory(Long clientId);

    AppointmentResponse createAppointment(Long clientId, AppointmentRequest request);

    void cancelAppointment(Long clientId, Long appointmentId);

    void cancelAppointment(Long clientId, Long appointmentId, String observation);

    AppointmentResponse acceptProposal(Long clientId, Long appointmentId);

    void rejectProposal(Long clientId, Long appointmentId);

    AppointmentResponse reschedule(Long clientId, Long appointmentId, RescheduleRequest request);

    List<UserResponse> listBarbers(Long clientId);

    List<ServiceResponse> listServicesByBarber(Long barberId);

    List<LocalDate> getBarberDateOff(Long clientId, Long barberId);

    List<com.americobarber.dto.response.AvailabilityResponse> getBarberAvailability(Long barberId);

    List<java.time.LocalTime> getAvailableTimes(Long barberId, LocalDate date, List<Long> serviceIds);
}
