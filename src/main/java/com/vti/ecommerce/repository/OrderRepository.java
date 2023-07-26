package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long id, Pageable pageable);

    @Query(value = "SELECT * from orders", nativeQuery = true)
    List<Order> findAllWithPage(Pageable pageable);
}
