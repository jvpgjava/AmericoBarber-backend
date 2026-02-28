package com.americobarber.repository;

import com.americobarber.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByBarberIdOrderByDayOfWeekAscStartTimeAsc(Long barberId);

    void deleteByBarberId(Long barberId);
}
