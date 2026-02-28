package com.americobarber.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "barber_date_off", indexes = {
    @jakarta.persistence.Index(name = "idx_barber_date_off_barber", columnList = "barber_id"),
    @jakarta.persistence.Index(name = "idx_barber_date_off_date", columnList = "date_off")
})
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class BarberDateOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barber_id", nullable = false)
    private User barber;

    @Column(name = "date_off", nullable = false)
    private LocalDate dateOff;
}
