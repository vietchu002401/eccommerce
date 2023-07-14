package com.vti.ecommerce.controller;

import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.CategoryService;
import com.vti.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping("/home/category-list")
    public ResponseEntity<ResponseData> getAllCategory(){
        return categoryService.getAllCategory();
    }

    @GetMapping("/product-list/{categoryId}")
    public ResponseEntity<ResponseData> getProductByCategory(@PathVariable Long categoryId){
        return productService.getProductByCategory(categoryId);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> searchByKeyword(@RequestParam String q){
        return productService.searchProduct(q);
    }

    @GetMapping("/best-sales")
    public ResponseEntity<ResponseData> getBestSeller(){
        return productService.getBestSeller();
    }

}
