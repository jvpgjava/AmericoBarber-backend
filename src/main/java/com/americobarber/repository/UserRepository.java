package com.americobarber.repository;

import com.americobarber.entity.User;
import com.americobarber.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCpf(String cpf);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByPhone(String phone);

    List<User> findByRole(UserRole role);

    List<User> findByRoleAndActiveTrue(UserRole role);

    List<User> findByIsBarberTrueAndActiveTrue();

    List<User> findByIsBarberTrue();

    List<User> findByAssignedBarber_IdAndActiveTrue(Long barberId);
}
