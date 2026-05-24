package com.americobarber.repository;

import com.americobarber.entity.CancellationPenalty;
import com.americobarber.enums.PenaltyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CancellationPenaltyRepository extends JpaRepository<CancellationPenalty, Long> {

    boolean existsByClientIdAndStatusIn(Long clientId, Collection<PenaltyStatus> statuses);

    Optional<CancellationPenalty> findFirstByClientIdAndStatusInOrderByCreatedAtDesc(
            Long clientId, Collection<PenaltyStatus> statuses);

    List<CancellationPenalty> findAllByOrderByCreatedAtDesc();
}
