package com.americobarber.service.impl;

import com.americobarber.dto.request.ReviewPenaltyRequest;
import com.americobarber.dto.request.SubmitPenaltyProofRequest;
import com.americobarber.dto.response.CancellationPenaltyResponse;
import com.americobarber.entity.Appointment;
import com.americobarber.entity.CancellationPenalty;
import com.americobarber.entity.User;
import com.americobarber.enums.PenaltyStatus;
import com.americobarber.exception.BusinessException;
import com.americobarber.exception.ResourceNotFoundException;
import com.americobarber.mapper.CancellationPenaltyMapper;
import com.americobarber.repository.CancellationPenaltyRepository;
import com.americobarber.repository.UserRepository;
import com.americobarber.service.CancellationPenaltyService;
import com.americobarber.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CancellationPenaltyServiceImpl implements CancellationPenaltyService {

    private static final EnumSet<PenaltyStatus> BLOCKING_STATUSES =
            EnumSet.of(PenaltyStatus.PENDING_PAYMENT, PenaltyStatus.REJECTED);

    private final CancellationPenaltyRepository penaltyRepository;
    private final UserRepository userRepository;
    private final CancellationPenaltyMapper penaltyMapper;
    private final SseService sseService;

    @Override
    @Transactional
    public void createPenaltyForLateCancellation(User client, Appointment appointment) {
        CancellationPenalty penalty = CancellationPenalty.builder()
                .client(client)
                .appointment(appointment)
                .amount(appointment.getTotalPrice())
                .status(PenaltyStatus.PENDING_PAYMENT)
                .build();
        penalty = penaltyRepository.save(penalty);
        sseService.broadcast("PENALTY_UPDATE", penalty.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isClientBlocked(Long clientId) {
        return penaltyRepository.existsByClientIdAndStatusIn(clientId, BLOCKING_STATUSES);
    }

    @Override
    @Transactional(readOnly = true)
    public CancellationPenaltyResponse getActivePenalty(Long clientId) {
        return penaltyRepository
                .findFirstByClientIdAndStatusInOrderByCreatedAtDesc(clientId, BLOCKING_STATUSES)
                .map(penaltyMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public CancellationPenaltyResponse submitProof(Long clientId, SubmitPenaltyProofRequest request) {
        CancellationPenalty penalty = penaltyRepository
                .findFirstByClientIdAndStatusInOrderByCreatedAtDesc(clientId, BLOCKING_STATUSES)
                .orElseThrow(() -> new BusinessException("Nenhuma penalidade pendente encontrada"));

        if (request.getProofImage() == null || request.getProofImage().isBlank()) {
            throw new BusinessException("Comprovante de pagamento é obrigatório");
        }

        penalty.setProofImage(request.getProofImage());
        penalty.setStatus(PenaltyStatus.PROOF_SUBMITTED);
        penalty.setProofSubmittedAt(Instant.now());
        penalty = penaltyRepository.save(penalty);
        sseService.broadcast("PENALTY_UPDATE", penalty.getId());
        return penaltyMapper.toResponse(penalty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CancellationPenaltyResponse> listAll() {
        return penaltyRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(penaltyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CancellationPenaltyResponse approve(Long penaltyId, Long adminId, ReviewPenaltyRequest request) {
        CancellationPenalty penalty = findPenalty(penaltyId);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User", adminId));

        penalty.setStatus(PenaltyStatus.APPROVED);
        penalty.setReviewedBy(admin);
        penalty.setReviewedAt(Instant.now());
        if (request != null && request.getAdminNotes() != null) {
            penalty.setAdminNotes(request.getAdminNotes());
        }
        penalty = penaltyRepository.save(penalty);
        sseService.broadcast("PENALTY_UPDATE", penalty.getId());
        return penaltyMapper.toResponse(penalty);
    }

    @Override
    @Transactional
    public CancellationPenaltyResponse reject(Long penaltyId, Long adminId, ReviewPenaltyRequest request) {
        CancellationPenalty penalty = findPenalty(penaltyId);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User", adminId));

        penalty.setStatus(PenaltyStatus.REJECTED);
        penalty.setReviewedBy(admin);
        penalty.setReviewedAt(Instant.now());
        if (request != null && request.getAdminNotes() != null) {
            penalty.setAdminNotes(request.getAdminNotes());
        }
        penalty = penaltyRepository.save(penalty);
        sseService.broadcast("PENALTY_UPDATE", penalty.getId());
        return penaltyMapper.toResponse(penalty);
    }

    @Override
    public boolean isLateCancellation(Appointment appointment) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());
        Duration untilAppointment = Duration.between(LocalDateTime.now(), appointmentDateTime);
        long hours = untilAppointment.toHours();
        return hours >= 0 && hours < LATE_CANCELLATION_HOURS;
    }

    private CancellationPenalty findPenalty(Long penaltyId) {
        return penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new ResourceNotFoundException("Penalidade", penaltyId));
    }
}
