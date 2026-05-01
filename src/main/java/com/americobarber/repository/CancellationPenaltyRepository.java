package com.americobarber.repository;

import com.americobarber.entity.CancellationPenalty;
import com.americobarber.enums.CancellationPenaltyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CancellationPenaltyRepository extends JpaRepository<CancellationPenalty, Long> {

    List<CancellationPenalty> findByClientIdAndStatusInOrderByCreatedAtDesc(Long clientId, List<CancellationPenaltyStatus> statuses);

    Optional<CancellationPenalty> findFirstByClientIdAndStatusInOrderByCreatedAtDesc(Long clientId, List<CancellationPenaltyStatus> statuses);

    boolean existsByClientIdAndStatusIn(Long clientId, List<CancellationPenaltyStatus> statuses);

    List<CancellationPenalty> findByStatusOrderByCreatedAtDesc(CancellationPenaltyStatus status);

    List<CancellationPenalty> findAllByOrderByCreatedAtDesc();

    boolean existsByAppointmentId(Long appointmentId);
}
