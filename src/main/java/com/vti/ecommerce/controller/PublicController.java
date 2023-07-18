package com.vti.ecommerce.controller;

import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.CartService;
import com.vti.ecommerce.service.CategoryService;
import com.vti.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class PublicController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;

    @GetMapping("/home/category-list")
    public ResponseEntity<ResponseData> getAllCategory(){
        return categoryService.getAllCategory();
    }

    @GetMapping("/product-list/{categoryId}")
    public ResponseEntity<ResponseData> getProductByCategory(@PathVariable Long categoryId){
        return productService.getProductByCategory(categoryId);
    }

    @PostMapping("/add-to-cart/{productId}")
    public ResponseEntity<ResponseData> addProductToCart(@PathVariable Long productId, HttpServletRequest httpServletRequest){
        return cartService.addToCart(productId, httpServletRequest);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> searchByKeyword(@RequestParam String q){
        return productService.searchProduct(q);
    }

    @GetMapping("/home/best-sales")
    public ResponseEntity<ResponseData> getBestSeller(){
        return productService.getBestSeller();
    }

}
