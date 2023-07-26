package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    boolean existsBySourceImage(String productImageUrl);
    List<ProductImage> findAllByProductId(Long productId);

    @Query(value = "UPDATE product_image SET status = false, updated_date = NOW() WHERE id = :productImageId", nativeQuery = true)
    @Modifying
    @Transactional
    void inActiveById(@Param("productImageId") Long productImageId);
}
