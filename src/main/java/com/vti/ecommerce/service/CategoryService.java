package com.vti.ecommerce.service;

import com.vti.ecommerce.dto.CategoryDTO;
import com.vti.ecommerce.model.Category;
import com.vti.ecommerce.repository.CategoryRepository;
import com.vti.ecommerce.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<ResponseData> createCategory(CategoryDTO categoryDTO) {
        try {
            if (categoryRepository.existsByName(categoryDTO.getName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .categoryImage(categoryDTO.getCategoryImage())
                .status(categoryDTO.isStatus())
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created category", categoryRepository.save(category)));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        try {
            Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
            if(categoryOptional.isPresent()){
              Category category = categoryOptional.get();
              if(!category.getName().equals(categoryDTO.getName())){
                  if (categoryRepository.existsByName(categoryDTO.getName())) {
                      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, "This name is already exist", categoryDTO));
                  }
              }
              category.setName(categoryDTO.getName());
              category.setCategoryImage(categoryDTO.getCategoryImage());
              category.setDescription(categoryDTO.getDescription());
              category.setUpdatedDate(new Date());
              return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Updated", categoryRepository.save(category)));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Category not found", null));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> deleteCategory(Long categoryId) {
        try {
            if(categoryRepository.existsById(categoryId)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Category not found", null));
            }
            categoryRepository.deleteById(categoryId);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Deleted", categoryId));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getCategoryDetail(Long categoryId) {
        try {
            Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
            if(categoryOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Category not found", null));
            }
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", categoryOptional.get()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> searchCategory(String q) {
        try {
            List<Category> categories = categoryRepository.searchCategoriesByKeyword(q);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", categories));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getAllCategory(){
        try {
            List<Category> categories = categoryRepository.findAll();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", categories));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }
}
