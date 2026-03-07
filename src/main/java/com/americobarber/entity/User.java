package com.americobarber.entity;

import com.americobarber.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @jakarta.persistence.Index(name = "idx_user_email", columnList = "email"),
    @jakarta.persistence.Index(name = "idx_user_cpf", columnList = "cpf"),
    @jakarta.persistence.Index(name = "idx_user_phone", columnList = "phone"),
    @jakarta.persistence.Index(name = "idx_user_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "is_barber", nullable = false)
    @Builder.Default
    private Boolean isBarber = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "slot_interval_minutes")
    @Builder.Default
    private Integer slotIntervalMinutes = 30;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_barber_id")
    private User assignedBarber;

    @OneToMany(mappedBy = "barber")
    @Builder.Default
    private List<ServiceEntity> services = new ArrayList<>();

    @OneToMany(mappedBy = "barber")
    @Builder.Default
    private List<Appointment> barberAppointments = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    @Builder.Default
    private List<Appointment> clientAppointments = new ArrayList<>();

    @OneToMany(mappedBy = "barber")
    @Builder.Default
    private List<Availability> availabilities = new ArrayList<>();

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
