package com.americobarber.repository;

import com.americobarber.entity.Appointment;
import com.americobarber.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientIdOrderByDateDescStartTimeDesc(Long clientId);

    List<Appointment> findByBarberIdOrderByDateAscStartTimeAsc(Long barberId);

    List<Appointment> findByBarberIdAndDateOrderByStartTimeAsc(Long barberId, LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.barber.id = :barberId AND a.date = :date " +
           "AND a.status IN (com.americobarber.enums.AppointmentStatus.AGENDADO, com.americobarber.enums.AppointmentStatus.PROPOSTA_REAGENDAMENTO) " +
           "AND (:excludeId IS NULL OR a.id != :excludeId) " +
           "AND ((a.startTime < :endTime AND a.endTime > :startTime))")
    List<Appointment> findOverlappingAppointments(
            @Param("barberId") Long barberId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );

    @Query("SELECT a FROM Appointment a WHERE a.id != :excludeId AND a.client.id = :clientId " +
           "AND a.date = :date AND a.startTime = :startTime " +
           "AND a.status IN (com.americobarber.enums.AppointmentStatus.AGENDADO, com.americobarber.enums.AppointmentStatus.PROPOSTA_REAGENDAMENTO)")
    List<Appointment> findDuplicateClientAppointment(
            @Param("clientId") Long clientId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("excludeId") Long excludeId
    );

    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.status IN " +
           "(com.americobarber.enums.AppointmentStatus.AGENDADO, com.americobarber.enums.AppointmentStatus.PROPOSTA_REAGENDAMENTO) " +
           "AND (a.date < :today OR (a.date = :today AND a.endTime <= :now))")
    List<Appointment> findOverdueAppointments(@Param("today") LocalDate today, @Param("now") LocalTime now);
}
