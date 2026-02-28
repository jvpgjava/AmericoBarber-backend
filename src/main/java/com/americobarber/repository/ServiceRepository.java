package com.americobarber.repository;

import com.americobarber.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findByBarberIdAndActiveTrue(Long barberId);

    List<ServiceEntity> findByActiveTrue();
}
