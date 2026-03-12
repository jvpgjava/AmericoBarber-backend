package com.americobarber.service;

import com.americobarber.dto.request.AvailabilityRequest;
import com.americobarber.dto.request.BarberDateOffRequest;
import com.americobarber.dto.request.ProposeRescheduleRequest;
import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.AvailabilityResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;

import java.time.LocalDate;
import java.util.List;

public interface BarberService {

    UserResponse getProfile(Long barberId);

    List<AppointmentResponse> myAppointments(Long barberId);

    List<AppointmentResponse> myHistory(Long barberId);

    List<UserResponse> listMyClients(Long barberId);

    void cancelByBarber(Long barberId, Long appointmentId, String barberMessage);

    AppointmentResponse proposeReschedule(Long barberId, Long appointmentId, ProposeRescheduleRequest request);

    List<AvailabilityResponse> getAvailability(Long barberId);

    List<AvailabilityResponse> setAvailability(Long barberId, List<AvailabilityRequest> requests);

    List<LocalDate> getDateOff(Long barberId);

    List<LocalDate> setDateOff(Long barberId, BarberDateOffRequest request);

    ServiceResponse updateMyService(Long barberId, Long serviceId, ServiceRequest request);

    List<ServiceResponse> listMyServices(Long barberId);

    UserResponse updateSlotInterval(Long barberId, com.americobarber.dto.request.SlotIntervalRequest request);

    AppointmentResponse finalizeAppointment(Long barberId, Long appointmentId);
}
