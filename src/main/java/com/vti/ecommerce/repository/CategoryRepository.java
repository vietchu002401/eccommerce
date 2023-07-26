package com.vti.ecommerce.repository;

import com.vti.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    @Query(value = "SELECT * FROM category WHERE name LIKE %:q% OR description LIKE %:q%", nativeQuery = true)
    List<Category> searchCategoriesByKeyword(@Param("q") String keyword);

    @Query(value = "UPDATE category SET status = false, updated_date = NOW() WHERE id = :categoryId", nativeQuery = true)
    @Modifying
    @Transactional
    void inActiveById(@Param("categoryId") Long categoryId);
}
