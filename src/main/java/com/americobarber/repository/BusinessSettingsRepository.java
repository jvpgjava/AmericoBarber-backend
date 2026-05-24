package com.americobarber.repository;

import com.americobarber.entity.BusinessSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessSettingsRepository extends JpaRepository<BusinessSettings, Long> {
}
