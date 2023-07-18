package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    boolean existsBySourceImage(String productImageUrl);
    List<ProductImage> findAllByProductId(Long productId);
}
