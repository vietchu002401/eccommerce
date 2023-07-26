package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    @Query(value = "SELECT * FROM product WHERE name LIKE %:q% OR description LIKE %:q%", nativeQuery = true)
    List<Product> searchProductByKeyword(@Param("q") String keyword, Pageable pageRequest);

    List<Product> findAllByCategoryId(Long categoryId, Pageable pageable);

    List<Product> findByIdIn(List<Long> productIds);

    @Query(value = "SELECT product.* FROM product " +
        "JOIN (SELECT product_id, SUM(quantity) AS totalQuantity FROM order_item " +
        "GROUP BY product_id ORDER BY totalQuantity DESC LIMIT 5) AS top_products ON product.id = top_products.product_id", nativeQuery = true)
    List<Product> findBestSeller();

    @Query(value = "SELECT * FROM product WHERE id = :productId AND amount < :quantity", nativeQuery = true)
    Optional<Product> findWhereAmountNotEnough(@Param("productId") Long productId,
                                               @Param("quantity") int quantity);

    @Query(value = "UPDATE product\n" +
        "SET amount = amount + :quantity, updated_date = NOW()\n" +
        "WHERE id = :productId", nativeQuery = true)
    @Modifying
    @Transactional
    int updateAmount(@Param("quantity") int quantity,
                     @Param("productId") Long productId);

    @Query(value = "SELECT * FROM product", nativeQuery = true)
    List<Product> findAllWithPage(Pageable pageable);
}
