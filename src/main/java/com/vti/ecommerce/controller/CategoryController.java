package com.vti.ecommerce.controller;


import com.vti.ecommerce.dto.CategoryDTO;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<ResponseData> createCategory(@RequestPart("categoryDTO") CategoryDTO categoryDTO, @RequestPart("files") MultipartFile file) {
        return categoryService.createCategory(categoryDTO, file);
    }

    @PostMapping("/update/{categoryId}")
    public ResponseEntity<ResponseData> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategory(categoryId, categoryDTO);
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<ResponseData> deleteCategory(@PathVariable Long categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @GetMapping("/detail/{categoryId}")
    public ResponseEntity<ResponseData> getCategoryDetail(@PathVariable Long categoryId){
        return categoryService.getCategoryDetail(categoryId);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> searchCategory(@RequestParam String q, int page){
        return categoryService.searchCategory(q, page);
    }

//    @PostMapping("/upload-image")
//    public ResponseEntity<ResponseData> uploadImage(@RequestParam("files") List<MultipartFile> files){
//        return categoryService.uploadImage(files);
//    }

}
