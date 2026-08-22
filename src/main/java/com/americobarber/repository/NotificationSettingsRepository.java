package com.americobarber.repository;

import com.americobarber.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
}
