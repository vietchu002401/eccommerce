package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.Coupon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Query(value = "SELECT * FROM coupon", nativeQuery = true)
    List<Coupon> findAllWithPage(Pageable pageable);

    boolean existsByCode(String code);

    @Query(value = "SELECT * FROM coupon WHERE code = :couponCode", nativeQuery = true)
    Optional<Coupon> findByCode(@Param("couponCode") String couponCode);
}
