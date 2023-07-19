package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByProductIdAndCartId(Long productId, Long cartId);

    List<CartItem> findALlByCartId(Long cartId);

    List<CartItem> findCartItemByIdIn(List<Long> cartItemId);

    @Query(value = "UPDATE cart_item\n" +
        "SET quantity = :quantity, sub_total = (SELECT price FROM product WHERE id = product_id) * :quantity, updated_date = NOW()\n" +
        "WHERE id = :cartItemId", nativeQuery = true)
    @Modifying
    @Transactional
    Integer updateQuantity(@Param("quantity") int quantity,
                        @Param("cartItemId") Long cartItemId);

    Optional<CartItem> findByProductId(Long productId);
}
