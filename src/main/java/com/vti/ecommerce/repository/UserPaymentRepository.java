package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.UserPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserPaymentRepository extends JpaRepository<UserPayment, Long> {
    boolean existsByUserId(Long userId);

    Optional<UserPayment> findByUserId(Long userId);

    @Query(value = "UPDATE user_payment SET status = false, updated_date = NOW() WHERE id = :userPaymentId", nativeQuery = true)
    @Modifying
    @Transactional
    void inActiveById(@Param("userPaymentId") Long userPaymentId);
}
