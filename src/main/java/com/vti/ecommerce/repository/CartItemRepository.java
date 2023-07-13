package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByProductIdAndCartId(Long productId, Long cartId);

    Optional<CartItem> findByProductId(Long productId);

    List<CartItem> findALlByCartId(Long cartId);

    List<CartItem> findCartItemByIdIn(List<Long> cartItemId);
}
