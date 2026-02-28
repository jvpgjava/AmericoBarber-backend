package com.americobarber.repository;

import com.americobarber.entity.BarberDateOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BarberDateOffRepository extends JpaRepository<BarberDateOff, Long> {

    List<BarberDateOff> findByBarberIdOrderByDateOffAsc(Long barberId);

    boolean existsByBarberIdAndDateOff(Long barberId, LocalDate dateOff);

    void deleteByBarberIdAndDateOff(Long barberId, LocalDate dateOff);
}
