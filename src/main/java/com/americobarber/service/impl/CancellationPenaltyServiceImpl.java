package com.americobarber.service.impl;

import com.americobarber.dto.response.CancellationPenaltyResponse;
import com.americobarber.entity.CancellationPenalty;
import com.americobarber.entity.User;
import com.americobarber.enums.CancellationPenaltyStatus;
import com.americobarber.exception.BusinessException;
import com.americobarber.exception.ResourceNotFoundException;
import com.americobarber.repository.CancellationPenaltyRepository;
import com.americobarber.repository.UserRepository;
import com.americobarber.service.CancellationPenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CancellationPenaltyServiceImpl implements CancellationPenaltyService {

    private static final List<CancellationPenaltyStatus> ACTIVE_STATUSES = List.of(
            CancellationPenaltyStatus.PENDING,
            CancellationPenaltyStatus.AWAITING_REVIEW
    );

    private final CancellationPenaltyRepository penaltyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CancellationPenaltyResponse getActivePenalty(Long clientId) {
        return penaltyRepository
                .findFirstByClientIdAndStatusInOrderByCreatedAtDesc(clientId, ACTIVE_STATUSES)
                .map(this::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public CancellationPenaltyResponse submitProof(Long clientId, Long penaltyId, String proofImageData) {
        CancellationPenalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new ResourceNotFoundException("Penalidade", penaltyId));

        if (!penalty.getClient().getId().equals(clientId)) {
            throw new BusinessException("Penalidade não pertence ao cliente");
        }
        if (penalty.getStatus() != CancellationPenaltyStatus.PENDING) {
            throw new BusinessException("Comprovante já foi enviado ou penalidade já foi revisada");
        }

        penalty.setProofImageData(proofImageData);
        penalty.setStatus(CancellationPenaltyStatus.AWAITING_REVIEW);
        penalty = penaltyRepository.save(penalty);
        return toResponse(penalty);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActivePenalty(Long clientId) {
        return penaltyRepository.existsByClientIdAndStatusIn(clientId, ACTIVE_STATUSES);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CancellationPenaltyResponse> listAll() {
        return penaltyRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CancellationPenaltyResponse> listByStatus(CancellationPenaltyStatus status) {
        return penaltyRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CancellationPenaltyResponse confirmPenalty(Long adminId, Long penaltyId) {
        CancellationPenalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new ResourceNotFoundException("Penalidade", penaltyId));

        if (penalty.getStatus() != CancellationPenaltyStatus.AWAITING_REVIEW) {
            throw new BusinessException("Penalidade não está aguardando revisão");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        penalty.setStatus(CancellationPenaltyStatus.CONFIRMED);
        penalty.setReviewedAt(Instant.now());
        penalty.setReviewedBy(admin);
        penalty = penaltyRepository.save(penalty);
        return toResponse(penalty);
    }

    @Override
    @Transactional
    public CancellationPenaltyResponse rejectPenalty(Long adminId, Long penaltyId) {
        CancellationPenalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new ResourceNotFoundException("Penalidade", penaltyId));

        if (penalty.getStatus() != CancellationPenaltyStatus.AWAITING_REVIEW) {
            throw new BusinessException("Penalidade não está aguardando revisão");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        penalty.setStatus(CancellationPenaltyStatus.REJECTED);
        penalty.setReviewedAt(Instant.now());
        penalty.setReviewedBy(admin);
        penalty = penaltyRepository.save(penalty);
        return toResponse(penalty);
    }

    @Override
    @Transactional
    public void blockClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", clientId));
        client.setBlocked(true);
        userRepository.save(client);
    }

    @Override
    @Transactional
    public void unblockClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", clientId));
        client.setBlocked(false);
        userRepository.save(client);
    }

    private CancellationPenaltyResponse toResponse(CancellationPenalty p) {
        String serviceNames = p.getAppointment().getServices().stream()
                .map(s -> s.getName())
                .collect(Collectors.joining(", "));

        return CancellationPenaltyResponse.builder()
                .id(p.getId())
                .clientId(p.getClient().getId())
                .clientName(p.getClient().getName())
                .clientPhone(p.getClient().getPhone())
                .appointmentId(p.getAppointment().getId())
                .barberName(p.getAppointment().getBarber().getName())
                .serviceNames(serviceNames)
                .appointmentDate(p.getAppointment().getDate())
                .appointmentTime(p.getAppointment().getStartTime())
                .amount(p.getAmount())
                .status(p.getStatus())
                .proofImageData(p.getProofImageData())
                .createdAt(p.getCreatedAt())
                .reviewedAt(p.getReviewedAt())
                .reviewedByName(p.getReviewedBy() != null ? p.getReviewedBy().getName() : null)
                .build();
    }
}
