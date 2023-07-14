package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    @Query(value = "SELECT * FROM product WHERE name LIKE %:q% OR description LIKE %:q%", nativeQuery = true)
    List<Product> searchProductByKeyword(@Param("q") String keyword);

    List<Product> findAllByCategoryId(Long categoryId);

    List<Product> findByIdIn(List<Long> productIds);
    @Query(value = "SELECT product.* FROM product JOIN (SELECT product_id, SUM(quantity) AS totalQuantity FROM order_item GROUP BY product_id ORDER BY totalQuantity DESC LIMIT 5) AS top_products ON product.id = top_products.product_id", nativeQuery = true)
    List<Product> findBestSeller();
}
