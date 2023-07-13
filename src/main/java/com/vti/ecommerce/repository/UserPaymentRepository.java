package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.UserPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPaymentRepository extends JpaRepository<UserPayment, Long> {
    boolean existsByUserId(Long userId);

    Optional<UserPayment> findByUserId(Long userId);
}
