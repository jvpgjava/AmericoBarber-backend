package com.americobarber.service.impl;

import com.americobarber.dto.request.AppointmentRequest;
import com.americobarber.dto.request.CreateBarberRequest;
import com.americobarber.dto.request.GalleryPhotoRequest;
import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.request.UserUpdateRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.GalleryPhotoResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.entity.Appointment;
import com.americobarber.entity.GalleryPhoto;
import com.americobarber.entity.ServiceEntity;
import com.americobarber.entity.User;
import com.americobarber.enums.AppointmentStatus;
import com.americobarber.enums.UserRole;
import com.americobarber.exception.BusinessException;
import com.americobarber.exception.ResourceNotFoundException;
import com.americobarber.mapper.AppointmentMapper;
import com.americobarber.mapper.GalleryPhotoMapper;
import com.americobarber.mapper.ServiceMapper;
import com.americobarber.mapper.UserMapper;
import com.americobarber.repository.AppointmentRepository;
import com.americobarber.repository.BarberDateOffRepository;
import com.americobarber.repository.GalleryPhotoRepository;
import com.americobarber.repository.ServiceRepository;
import com.americobarber.repository.UserRepository;
import com.americobarber.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final BarberDateOffRepository barberDateOffRepository;
    private final GalleryPhotoRepository galleryPhotoRepository;
    private final UserMapper userMapper;
    private final ServiceMapper serviceMapper;
    private final AppointmentMapper appointmentMapper;
    private final GalleryPhotoMapper galleryPhotoMapper;
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
                .description(request.getDescription())
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
        if (request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());
        if (request.getDescription() != null) {
            if (!request.getDescription().equals(user.getDescription())) {
                user.setDescriptionUpdatedAt(java.time.Instant.now());
            }
            user.setDescription(request.getDescription());
        }
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public AppointmentResponse createAppointmentForClient(AppointmentRequest request) {
        if (request.getClientId() == null || request.getBarberId() == null || request.getServiceIds() == null || request.getServiceIds().isEmpty()) {
            throw new BusinessException("Cliente, barbeiro e pelo menos um serviço são obrigatórios");
        }
        if (request.getDate() == null || request.getStartTime() == null) {
            throw new BusinessException("Data e horário são obrigatórios");
        }

        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getClientId()));
        User barber = userRepository.findById(request.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException("Barbeiro", request.getBarberId()));

        List<ServiceEntity> services = serviceRepository.findAllById(request.getServiceIds());
        if (services.size() < request.getServiceIds().size()) {
            throw new BusinessException("Um ou mais serviços informados não foram encontrados");
        }

        java.math.BigDecimal totalPrice = java.math.BigDecimal.ZERO;
        int totalDuration = 0;
        for (ServiceEntity service : services) {
            if (!Boolean.TRUE.equals(service.getActive())) {
                throw new BusinessException("Serviço inativo: " + service.getName());
            }
            if (!service.getBarber().getId().equals(barber.getId())) {
                throw new BusinessException("Serviço " + service.getName() + " não pertence ao barbeiro informado");
            }
            if (service.getPrice() != null) {
                totalPrice = totalPrice.add(service.getPrice());
            }
            totalDuration += (service.getDurationMinutes() != null ? service.getDurationMinutes() : 60);
        }

        if (barberDateOffRepository.existsByBarberIdAndDateOff(request.getBarberId(), request.getDate())) {
            throw new BusinessException("O barbeiro não atende nesta data.");
        }

        LocalTime endTime = request.getStartTime().plusMinutes(totalDuration);

        List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(
                request.getBarberId(), request.getDate(), request.getStartTime(), endTime, null);
        if (!overlapping.isEmpty()) {
            throw new BusinessException("Horário já ocupado para este barbeiro");
        }

        Appointment appointment = Appointment.builder()
                .client(client)
                .barber(barber)
                .services(services)
                .totalPrice(totalPrice)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .status(AppointmentStatus.AGENDADO)
                .observation(request.getObservation())
                .build();

        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(appointment);
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

    // ================= GALLERY =================

    @Override
    @Transactional(readOnly = true)
    public List<GalleryPhotoResponse> listGalleryPhotos() {
        return galleryPhotoRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(galleryPhotoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GalleryPhotoResponse addGalleryPhoto(GalleryPhotoRequest request) {
        GalleryPhoto photo = galleryPhotoMapper.toEntity(request);
        if (photo.getDisplayOrder() == null) {
            photo.setDisplayOrder(0);
        }
        photo = galleryPhotoRepository.save(photo);
        return galleryPhotoMapper.toResponse(photo);
    }

    @Override
    @Transactional
    public GalleryPhotoResponse updateGalleryPhoto(Long id, GalleryPhotoRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID da foto inválido");
        }
        GalleryPhoto photo = galleryPhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GalleryPhoto", id));
        if (request.getImageData() != null && !request.getImageData().isBlank()) {
            photo.setImageData(request.getImageData());
        }
        if (request.getTitle() != null) {
            photo.setTitle(request.getTitle());
        }
        if (request.getDisplayOrder() != null) {
            photo.setDisplayOrder(request.getDisplayOrder());
        }
        photo = galleryPhotoRepository.save(photo);
        return galleryPhotoMapper.toResponse(photo);
    }

    @Override
    @Transactional
    public void deleteGalleryPhoto(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID da foto inválido");
        }
        if (!galleryPhotoRepository.existsById(id)) {
            throw new ResourceNotFoundException("GalleryPhoto", id);
        }
        galleryPhotoRepository.deleteById(id);
    }
}
