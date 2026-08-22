package com.americobarber.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettings {

    @Id
    private Long id = 1L;

    @Column(name = "confirmation_enabled", nullable = false)
    private Boolean confirmationEnabled;

    @Column(name = "reminder_enabled", nullable = false)
    private Boolean reminderEnabled;

    @Column(name = "reminder_minutes_before", nullable = false)
    private Integer reminderMinutesBefore;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
