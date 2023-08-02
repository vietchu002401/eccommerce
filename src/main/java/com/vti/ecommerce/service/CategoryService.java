package com.vti.ecommerce.service;

import com.vti.ecommerce.dto.CategoryDTO;
import com.vti.ecommerce.exception.ConflictException;
import com.vti.ecommerce.exception.NotFoundException;
import com.vti.ecommerce.exception.ServerErrorException;
import com.vti.ecommerce.model.Category;
import com.vti.ecommerce.repository.CategoryRepository;
import com.vti.ecommerce.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileService fileService;

    public ResponseEntity<ResponseData> createCategory(CategoryDTO categoryDTO, MultipartFile file) {
        try {
            if (categoryRepository.existsByName(categoryDTO.getName())) {
                throw new ConflictException("Category name is already exist");
            }
            String imagePath = "";
            if (!file.isEmpty()) {
                imagePath = fileService.save(file);
            }
            Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .categoryImage(imagePath)
                .status(categoryDTO.isStatus())
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created category", categoryRepository.save(category)));
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        try {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
            if (!category.getName().equals(categoryDTO.getName())) {
                if (categoryRepository.existsByName(categoryDTO.getName())) {
                    throw new ConflictException("Category name is already exist");
                }
            }
            category.setName(categoryDTO.getName());
            category.setCategoryImage(categoryDTO.getCategoryImage());
            category.setDescription(categoryDTO.getDescription());
            category.setUpdatedDate(new Date());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Updated", categoryRepository.save(category)));
        } catch (NotFoundException | ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> deleteCategory(Long categoryId) {
        try {
            if (categoryRepository.existsById(categoryId)) {
                throw new NotFoundException("Category not found");
            }
            categoryRepository.deleteById(categoryId);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Deleted", categoryId));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> getCategoryDetail(Long categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", category));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> searchCategory(String q, int page) {
        try {
            Pageable pageable = PageRequest.of(page, 8);
            List<Category> categories = categoryRepository.searchCategoriesByKeyword(q, pageable);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", categories));
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> getAllCategory(int page) {
        try {
            Pageable pageable = PageRequest.of(page, 8);
            List<Category> categories = categoryRepository.findAllWithPage(pageable);
            if (categories.isEmpty()) {
                throw new NotFoundException("Category not found");
            }
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", categories));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }
}
