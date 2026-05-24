package com.americobarber.service;

import com.americobarber.dto.request.ReviewPenaltyRequest;
import com.americobarber.dto.request.SubmitPenaltyProofRequest;
import com.americobarber.dto.response.CancellationPenaltyResponse;
import com.americobarber.entity.Appointment;
import com.americobarber.entity.User;

import java.util.List;

public interface CancellationPenaltyService {

    int LATE_CANCELLATION_HOURS = 12;

    void createPenaltyForLateCancellation(User client, Appointment appointment);

    boolean isClientBlocked(Long clientId);

    CancellationPenaltyResponse getActivePenalty(Long clientId);

    CancellationPenaltyResponse submitProof(Long clientId, SubmitPenaltyProofRequest request);

    List<CancellationPenaltyResponse> listAll();

    CancellationPenaltyResponse approve(Long penaltyId, Long adminId, ReviewPenaltyRequest request);

    CancellationPenaltyResponse reject(Long penaltyId, Long adminId, ReviewPenaltyRequest request);

    boolean isLateCancellation(Appointment appointment);
}
