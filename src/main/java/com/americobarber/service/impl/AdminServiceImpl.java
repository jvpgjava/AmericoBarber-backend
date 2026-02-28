package com.americobarber.service.impl;

import com.americobarber.dto.request.CreateBarberRequest;
import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.request.UserUpdateRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.entity.ServiceEntity;
import com.americobarber.entity.User;
import com.americobarber.enums.UserRole;
import com.americobarber.exception.BusinessException;
import com.americobarber.exception.ResourceNotFoundException;
import com.americobarber.mapper.AppointmentMapper;
import com.americobarber.mapper.ServiceMapper;
import com.americobarber.mapper.UserMapper;
import com.americobarber.repository.AppointmentRepository;
import com.americobarber.repository.ServiceRepository;
import com.americobarber.repository.UserRepository;
import com.americobarber.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserMapper userMapper;
    private final ServiceMapper serviceMapper;
    private final AppointmentMapper appointmentMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createBarber(CreateBarberRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Telefone já cadastrado");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_ADMIN)
                .isBarber(true)
                .active(true)
                .build();
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID do usuário inválido");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (request.getName() != null) {
            if (request.getName().isBlank()) throw new BusinessException("Nome não pode ser vazio");
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            if (request.getEmail().isBlank()) throw new BusinessException("Email não pode ser vazio");
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 6) throw new BusinessException("Senha deve ter no mínimo 6 caracteres");
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getActive() != null) user.setActive(request.getActive());
        if (request.getAssignedBarberId() != null && user.getRole() == UserRole.ROLE_CLIENT) {
            if (request.getAssignedBarberId() <= 0) {
                throw new BusinessException("ID do barbeiro inválido");
            }
            User barber = userRepository.findById(request.getAssignedBarberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Barbeiro", request.getAssignedBarberId()));
            if (!Boolean.TRUE.equals(barber.getIsBarber())) {
                throw new BusinessException("O usuário informado não é barbeiro");
            }
            if (!Boolean.TRUE.equals(barber.getActive())) {
                throw new BusinessException("O barbeiro informado está inativo");
            }
            user.setAssignedBarber(barber);
        }
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listBarbers() {
        return userRepository.findByIsBarberTrueAndActiveTrue().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listClients() {
        return userRepository.findByRole(UserRole.ROLE_CLIENT).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceResponse createService(ServiceRequest request) {
        if (request.getBarberId() == null || request.getBarberId() <= 0) {
            throw new BusinessException("Barbeiro é obrigatório");
        }
        User barber = userRepository.findById(request.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException("Barber", request.getBarberId()));
        if (!Boolean.TRUE.equals(barber.getIsBarber())) {
            throw new BusinessException("O usuário informado não é barbeiro");
        }
        if (!Boolean.TRUE.equals(barber.getActive())) {
            throw new BusinessException("O barbeiro informado está inativo");
        }
        ServiceEntity entity = serviceMapper.toEntity(request);
        entity.setBarber(barber);
        entity = serviceRepository.save(entity);
        return serviceMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID do serviço inválido");
        }
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
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
        if (request.getBarberId() != null) {
            if (request.getBarberId() <= 0) throw new BusinessException("ID do barbeiro inválido");
            User barber = userRepository.findById(request.getBarberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Barber", request.getBarberId()));
            if (!Boolean.TRUE.equals(barber.getIsBarber())) {
                throw new BusinessException("O usuário informado não é barbeiro");
            }
            if (!Boolean.TRUE.equals(barber.getActive())) {
                throw new BusinessException("O barbeiro informado está inativo");
            }
            entity.setBarber(barber);
        }
        entity = serviceRepository.save(entity);
        return serviceMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> listAllServices() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> listAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
