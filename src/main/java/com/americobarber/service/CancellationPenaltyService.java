package com.americobarber.service;

import com.americobarber.dto.response.CancellationPenaltyResponse;

import java.util.List;

public interface CancellationPenaltyService {

    // Client-facing
    CancellationPenaltyResponse getActivePenalty(Long clientId);

    CancellationPenaltyResponse submitProof(Long clientId, Long penaltyId, String proofImageData);

    boolean hasActivePenalty(Long clientId);

    // Admin-facing
    List<CancellationPenaltyResponse> listAll();

    List<CancellationPenaltyResponse> listByStatus(com.americobarber.enums.CancellationPenaltyStatus status);

    CancellationPenaltyResponse confirmPenalty(Long adminId, Long penaltyId);

    CancellationPenaltyResponse rejectPenalty(Long adminId, Long penaltyId);

    void blockClient(Long clientId);

    void unblockClient(Long clientId);
}
