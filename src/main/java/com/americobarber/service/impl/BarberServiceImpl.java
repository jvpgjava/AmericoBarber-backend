package com.americobarber.service.impl;

import com.americobarber.dto.request.AvailabilityRequest;
import com.americobarber.dto.request.BarberDateOffRequest;
import com.americobarber.dto.request.ProposeRescheduleRequest;
import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.AvailabilityResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.entity.Appointment;
import com.americobarber.entity.Availability;
import com.americobarber.entity.BarberDateOff;
import com.americobarber.entity.ServiceEntity;
import com.americobarber.entity.User;
import com.americobarber.enums.AppointmentStatus;
import com.americobarber.exception.BusinessException;
import com.americobarber.exception.ResourceNotFoundException;
import com.americobarber.mapper.AppointmentMapper;
import com.americobarber.mapper.AvailabilityMapper;
import com.americobarber.mapper.ServiceMapper;
import com.americobarber.mapper.UserMapper;
import com.americobarber.repository.AppointmentRepository;
import com.americobarber.repository.AvailabilityRepository;
import com.americobarber.repository.BarberDateOffRepository;
import com.americobarber.repository.ServiceRepository;
import com.americobarber.repository.UserRepository;
import com.americobarber.service.BarberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarberServiceImpl implements BarberService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final AvailabilityRepository availabilityRepository;
    private final BarberDateOffRepository barberDateOffRepository;
    private final ServiceRepository serviceRepository;
    private final UserMapper userMapper;
    private final AppointmentMapper appointmentMapper;
    private final AvailabilityMapper availabilityMapper;
    private final ServiceMapper serviceMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long barberId) {
        User user = userRepository.findById(barberId).orElseThrow(() -> new ResourceNotFoundException("User", barberId));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> myAppointments(Long barberId) {
        return appointmentRepository.findByBarberIdOrderByDateAscStartTimeAsc(barberId).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AGENDADO || a.getStatus() == AppointmentStatus.PROPOSTA_REAGENDAMENTO)
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> myHistory(Long barberId) {
        return appointmentRepository.findByBarberIdOrderByDateAscStartTimeAsc(barberId).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listMyClients(Long barberId) {
        return userRepository.findByAssignedBarber_IdAndActiveTrue(barberId).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelByBarber(Long barberId, Long appointmentId, String barberMessage) {
        if (appointmentId == null || appointmentId <= 0) {
            throw new BusinessException("ID do agendamento inválido");
        }
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", appointmentId));
        if (!appointment.getBarber().getId().equals(barberId)) {
            throw new BusinessException("Agendamento não pertence a este barbeiro");
        }
        if (appointment.getStatus() != AppointmentStatus.AGENDADO && appointment.getStatus() != AppointmentStatus.PROPOSTA_REAGENDAMENTO) {
            throw new BusinessException("Apenas agendamentos ativos podem ser cancelados");
        }
        appointment.setStatus(AppointmentStatus.CANCELADO_POR_BARBEIRO);
        appointment.setBarberMessage(barberMessage);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse proposeReschedule(Long barberId, Long appointmentId, ProposeRescheduleRequest request) {
        if (appointmentId == null || appointmentId <= 0) {
            throw new BusinessException("ID do agendamento inválido");
        }
        if (request.getProposedDate() == null || request.getProposedStartTime() == null) {
            throw new BusinessException("Data e horário propostos são obrigatórios");
        }
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", appointmentId));
        if (!appointment.getBarber().getId().equals(barberId)) {
            throw new BusinessException("Agendamento não pertence a este barbeiro");
        }
        if (appointment.getStatus() != AppointmentStatus.AGENDADO && appointment.getStatus() != AppointmentStatus.PROPOSTA_REAGENDAMENTO) {
            throw new BusinessException("Apenas agendamentos ativos podem receber proposta de reagendamento");
        }
        LocalDate today = LocalDate.now();
        if (request.getProposedDate().isBefore(today)) {
            throw new BusinessException("Não é possível propor data passada.");
        }
        if (request.getProposedDate().equals(today) && !request.getProposedStartTime().isAfter(LocalTime.now())) {
            throw new BusinessException("Escolha um horário futuro.");
        }
        int duration = appointment.getServices().stream()
                .mapToInt(s -> s.getDurationMinutes() != null ? s.getDurationMinutes() : 60)
                .sum();
        LocalTime proposedEnd = request.getProposedStartTime().plusMinutes(duration);
        List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(
                barberId, request.getProposedDate(), request.getProposedStartTime(), proposedEnd, appointmentId);
        if (!overlapping.isEmpty()) {
            throw new BusinessException("Horário proposto já está ocupado");
        }
        appointment.setProposedDate(request.getProposedDate());
        appointment.setProposedStartTime(request.getProposedStartTime());
        appointment.setProposedEndTime(proposedEnd);
        appointment.setBarberMessage(request.getBarberMessage());
        appointment.setStatus(AppointmentStatus.PROPOSTA_REAGENDAMENTO);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAvailability(Long barberId) {
        return availabilityRepository.findByBarberIdOrderByDayOfWeekAscStartTimeAsc(barberId).stream()
                .map(availabilityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AvailabilityResponse> setAvailability(Long barberId, List<AvailabilityRequest> requests) {
        User barber = userRepository.findById(barberId).orElseThrow(() -> new ResourceNotFoundException("Barber", barberId));
        if (requests == null) {
            throw new BusinessException("Lista de disponibilidade não pode ser nula");
        }
        availabilityRepository.deleteByBarberId(barberId);
        for (AvailabilityRequest req : requests) {
            if (req.getDayOfWeek() == null || req.getDayOfWeek() < 1 || req.getDayOfWeek() > 7) {
                throw new BusinessException("Dia da semana deve ser entre 1 (Segunda) e 7 (Domingo)");
            }
            if (req.getStartTime() == null || req.getEndTime() == null) {
                throw new BusinessException("Horário de início e fim são obrigatórios");
            }
            if (req.getEndTime().isBefore(req.getStartTime()) || req.getEndTime().equals(req.getStartTime())) {
                throw new BusinessException("Horário de fim deve ser posterior ao início");
            }
            Availability av = availabilityMapper.toEntity(req);
            av.setBarber(barber);
            availabilityRepository.save(av);
        }
        return availabilityRepository.findByBarberIdOrderByDayOfWeekAscStartTimeAsc(barberId).stream()
                .map(availabilityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> getDateOff(Long barberId) {
        return barberDateOffRepository.findByBarberIdOrderByDateOffAsc(barberId).stream()
                .map(BarberDateOff::getDateOff)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<LocalDate> setDateOff(Long barberId, BarberDateOffRequest request) {
        User barber = userRepository.findById(barberId).orElseThrow(() -> new ResourceNotFoundException("Barbeiro", barberId));
        LocalDate today = LocalDate.now();
        if (request.getDatesOff() != null) {
            for (LocalDate d : request.getDatesOff()) {
                if (d != null && d.isBefore(today)) {
                    throw new BusinessException("Dias de folga devem ser hoje ou datas futuras.");
                }
            }
        }
        barberDateOffRepository.findByBarberIdOrderByDateOffAsc(barberId).forEach(barberDateOffRepository::delete);
        if (request.getDatesOff() != null) {
            for (LocalDate d : request.getDatesOff()) {
                if (d != null && !d.isBefore(today) && !barberDateOffRepository.existsByBarberIdAndDateOff(barberId, d)) {
                    barberDateOffRepository.save(BarberDateOff.builder().barber(barber).dateOff(d).build());
                }
            }
        }
        return getDateOff(barberId);
    }

    @Override
    @Transactional
    public ServiceResponse updateMyService(Long barberId, Long serviceId, ServiceRequest request) {
        if (serviceId == null || serviceId <= 0) {
            throw new BusinessException("ID do serviço inválido");
        }
        ServiceEntity entity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço", serviceId));
        if (!entity.getBarber().getId().equals(barberId)) {
            throw new BusinessException("Serviço não pertence a este barbeiro");
        }
        if (request.getName() != null) {
            if (request.getName().isBlank()) throw new BusinessException("Nome do serviço não pode ser vazio");
            entity.setName(request.getName());
        }
        if (request.getPrice() != null) {
            if (request.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new BusinessException("Preço não pode ser negativo");
            }
            entity.setPrice(request.getPrice());
        }
        if (request.getDurationMinutes() != null) {
            if (request.getDurationMinutes() <= 0) throw new BusinessException("Duração deve ser positiva (minutos)");
            entity.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getActive() != null) entity.setActive(request.getActive());
        entity = serviceRepository.save(entity);
        return serviceMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> listMyServices(Long barberId) {
        return serviceRepository.findByBarberIdAndActiveTrue(barberId).stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateSlotInterval(Long barberId, com.americobarber.dto.request.SlotIntervalRequest request) {
        User barber = userRepository.findById(barberId).orElseThrow(() -> new ResourceNotFoundException("Barbeiro", barberId));
        barber.setSlotIntervalMinutes(request.getSlotIntervalMinutes());
        barber = userRepository.save(barber);
        return userMapper.toResponse(barber);
    }
}
