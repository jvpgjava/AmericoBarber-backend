package com.americobarber.service.impl;

import com.americobarber.dto.request.AppointmentRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.entity.Appointment;
import com.americobarber.entity.BarberDateOff;
import com.americobarber.entity.ServiceEntity;
import com.americobarber.entity.User;
import com.americobarber.enums.AppointmentStatus;
import com.americobarber.exception.BusinessException;
import com.americobarber.exception.ResourceNotFoundException;
import com.americobarber.mapper.AppointmentMapper;
import com.americobarber.mapper.ServiceMapper;
import com.americobarber.mapper.UserMapper;
import com.americobarber.repository.AppointmentRepository;
import com.americobarber.repository.BarberDateOffRepository;
import com.americobarber.repository.ServiceRepository;
import com.americobarber.repository.UserRepository;
import com.americobarber.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final BarberDateOffRepository barberDateOffRepository;
    private final UserMapper userMapper;
    private final ServiceMapper serviceMapper;
    private final AppointmentMapper appointmentMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long clientId) {
        User user = userRepository.findById(clientId).orElseThrow(() -> new ResourceNotFoundException("User", clientId));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> myAppointments(Long clientId) {
        return appointmentRepository.findByClientIdOrderByDateDescStartTimeDesc(clientId).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AGENDADO || a.getStatus() == AppointmentStatus.PROPOSTA_REAGENDAMENTO)
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> myHistory(Long clientId) {
        return appointmentRepository.findByClientIdOrderByDateDescStartTimeDesc(clientId).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentResponse createAppointment(Long clientId, AppointmentRequest request) {
        if (request.getClientId() == null || request.getBarberId() == null || request.getServiceId() == null) {
            throw new BusinessException("Cliente, barbeiro e serviço são obrigatórios");
        }
        if (request.getDate() == null || request.getStartTime() == null) {
            throw new BusinessException("Data e horário são obrigatórios");
        }
        if (!clientId.equals(request.getClientId())) {
            throw new BusinessException("Cliente não autorizado");
        }
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getClientId()));
        if (client.getAssignedBarber() != null && !client.getAssignedBarber().getId().equals(request.getBarberId())) {
            throw new BusinessException("Cliente vinculado a outro barbeiro. Você só pode agendar com seu barbeiro.");
        }
        User barber = userRepository.findById(request.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException("Barbeiro", request.getBarberId()));
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço", request.getServiceId()));
        if (!Boolean.TRUE.equals(service.getActive())) {
            throw new BusinessException("Serviço inativo");
        }
        if (!service.getBarber().getId().equals(barber.getId())) {
            throw new BusinessException("Serviço não pertence ao barbeiro informado");
        }
        if (barberDateOffRepository.existsByBarberIdAndDateOff(request.getBarberId(), request.getDate())) {
            throw new BusinessException("O barbeiro não atende nesta data. Escolha outro dia.");
        }
        LocalDate today = LocalDate.now();
        if (request.getDate().isBefore(today)) {
            throw new BusinessException("Não é possível agendar em data passada.");
        }
        if (request.getDate().equals(today) && !request.getStartTime().isAfter(LocalTime.now())) {
            throw new BusinessException("Escolha um horário futuro.");
        }
        int duration = service.getDurationMinutes() != null ? service.getDurationMinutes() : 60;
        LocalTime endTime = request.getStartTime().plusMinutes(duration);
        List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(
                request.getBarberId(), request.getDate(), request.getStartTime(), endTime, null);
        if (!overlapping.isEmpty()) {
            throw new BusinessException("Horário já ocupado para este barbeiro");
        }
        List<Appointment> duplicate = appointmentRepository.findDuplicateClientAppointment(
                clientId, request.getDate(), request.getStartTime(), 0L);
        if (!duplicate.isEmpty()) {
            throw new BusinessException("Cliente já possui agendamento no mesmo horário");
        }
        Appointment appointment = Appointment.builder()
                .client(client)
                .barber(barber)
                .service(service)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .status(AppointmentStatus.AGENDADO)
                .build();
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long clientId, Long appointmentId) {
        cancelAppointment(clientId, appointmentId, null);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long clientId, Long appointmentId, String observation) {
        if (appointmentId == null || appointmentId <= 0) {
            throw new BusinessException("ID do agendamento inválido");
        }
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", appointmentId));
        if (!appointment.getClient().getId().equals(clientId)) {
            throw new BusinessException("Agendamento não pertence ao cliente");
        }
        if (appointment.getStatus() != AppointmentStatus.AGENDADO && appointment.getStatus() != AppointmentStatus.PROPOSTA_REAGENDAMENTO) {
            throw new BusinessException("Apenas agendamentos ativos ou com proposta pendente podem ser cancelados");
        }
        appointment.setStatus(AppointmentStatus.CANCELADO_POR_CLIENTE);
        appointment.setObservation(observation);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse acceptProposal(Long clientId, Long appointmentId) {
        if (appointmentId == null || appointmentId <= 0) {
            throw new BusinessException("ID do agendamento inválido");
        }
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", appointmentId));
        if (!appointment.getClient().getId().equals(clientId)) {
            throw new BusinessException("Agendamento não pertence ao cliente");
        }
        if (appointment.getStatus() != AppointmentStatus.PROPOSTA_REAGENDAMENTO || appointment.getProposedDate() == null) {
            throw new BusinessException("Não há proposta de reagendamento para aceitar");
        }
        appointment.setDate(appointment.getProposedDate());
        appointment.setStartTime(appointment.getProposedStartTime());
        appointment.setEndTime(appointment.getProposedEndTime());
        appointment.setStatus(AppointmentStatus.AGENDADO);
        appointment.setProposedDate(null);
        appointment.setProposedStartTime(null);
        appointment.setProposedEndTime(null);
        appointment.setBarberMessage(null);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional
    public void rejectProposal(Long clientId, Long appointmentId) {
        if (appointmentId == null || appointmentId <= 0) {
            throw new BusinessException("ID do agendamento inválido");
        }
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", appointmentId));
        if (!appointment.getClient().getId().equals(clientId)) {
            throw new BusinessException("Agendamento não pertence ao cliente");
        }
        if (appointment.getStatus() != AppointmentStatus.PROPOSTA_REAGENDAMENTO) {
            throw new BusinessException("Não há proposta de reagendamento para rejeitar");
        }
        appointment.setStatus(AppointmentStatus.CANCELADO_POR_CLIENTE);
        appointment.setProposedDate(null);
        appointment.setProposedStartTime(null);
        appointment.setProposedEndTime(null);
        appointment.setBarberMessage(null);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse reschedule(Long clientId, Long appointmentId, com.americobarber.dto.request.RescheduleRequest request) {
        if (appointmentId == null || appointmentId <= 0) {
            throw new BusinessException("ID do agendamento inválido");
        }
        if (request.getNewDate() == null || request.getNewStartTime() == null) {
            throw new BusinessException("Nova data e horário são obrigatórios");
        }
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", appointmentId));
        if (!appointment.getClient().getId().equals(clientId)) {
            throw new BusinessException("Agendamento não pertence ao cliente");
        }
        if (appointment.getStatus() != AppointmentStatus.AGENDADO && appointment.getStatus() != AppointmentStatus.PROPOSTA_REAGENDAMENTO) {
            throw new BusinessException("Apenas agendamentos ativos podem ser reagendados");
        }
        LocalDate today = LocalDate.now();
        if (request.getNewDate().isBefore(today)) {
            throw new BusinessException("Não é possível reagendar para data passada.");
        }
        if (request.getNewDate().equals(today) && !request.getNewStartTime().isAfter(LocalTime.now())) {
            throw new BusinessException("Escolha um horário futuro.");
        }
        int duration = appointment.getService().getDurationMinutes() != null ? appointment.getService().getDurationMinutes() : 60;
        java.time.LocalTime newEndTime = request.getNewStartTime().plusMinutes(duration);
        List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(
                appointment.getBarber().getId(), request.getNewDate(), request.getNewStartTime(), newEndTime, appointmentId);
        if (!overlapping.isEmpty()) {
            throw new BusinessException("Novo horário já ocupado para este barbeiro");
        }
        appointment.setDate(request.getNewDate());
        appointment.setStartTime(request.getNewStartTime());
        appointment.setEndTime(newEndTime);
        appointment.setObservation(request.getObservation());
        appointment.setStatus(AppointmentStatus.AGENDADO);
        appointment.setProposedDate(null);
        appointment.setProposedStartTime(null);
        appointment.setProposedEndTime(null);
        appointment.setBarberMessage(null);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listBarbers(Long clientId) {
        User client = userRepository.findById(clientId).orElseThrow(() -> new ResourceNotFoundException("Cliente", clientId));
        if (client.getAssignedBarber() != null) {
            return java.util.List.of(userMapper.toResponse(client.getAssignedBarber()));
        }
        return userRepository.findByIsBarberTrueAndActiveTrue().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> listServicesByBarber(Long barberId) {
        return serviceRepository.findByBarberIdAndActiveTrue(barberId).stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> getBarberDateOff(Long clientId, Long barberId) {
        if (barberId == null || barberId <= 0) {
            throw new BusinessException("ID do barbeiro inválido");
        }
        User client = userRepository.findById(clientId).orElseThrow(() -> new ResourceNotFoundException("Cliente", clientId));
        if (client.getAssignedBarber() != null && !client.getAssignedBarber().getId().equals(barberId)) {
            throw new BusinessException("Você só pode consultar disponibilidade do seu barbeiro");
        }
        return barberDateOffRepository.findByBarberIdOrderByDateOffAsc(barberId).stream()
                .map(BarberDateOff::getDateOff)
                .collect(Collectors.toList());
    }
}
