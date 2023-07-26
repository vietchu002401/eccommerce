package com.vti.ecommerce.controller;

import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.CartService;
import com.vti.ecommerce.service.CategoryService;
import com.vti.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ResponseData> getProductByCategory(@PathVariable Long categoryId, @RequestParam int page, int size){
        return productService.getProductByCategory(categoryId, page, size);
    }

    @PostMapping("/add-to-cart/{productId}")
    public ResponseEntity<ResponseData> addProductToCart(@PathVariable Long productId, HttpServletRequest httpServletRequest){
        return cartService.addToCart(productId, httpServletRequest);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> searchByKeyword(@RequestParam String q, int page, int size){
        return productService.searchProduct(q, page, size);
    }

    @GetMapping("/home/best-sales")
    public ResponseEntity<ResponseData> getBestSeller(){
        return productService.getBestSeller();
    }

    @GetMapping("/product/detail/{productId}")
    public ResponseEntity<ResponseData> getProductDetail(@PathVariable Long productId){
        return productService.getProductDetail(productId);
    }
}
