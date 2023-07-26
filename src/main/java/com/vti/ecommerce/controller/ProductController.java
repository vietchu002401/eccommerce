package com.vti.ecommerce.controller;

import com.vti.ecommerce.dto.ProductDTO;
import com.vti.ecommerce.dto.ProductRequestDTO;
import com.vti.ecommerce.model.Product;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ResponseData> getALlProduct(@RequestParam int page){
        return productService.getAllProduct(page);
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseData> createProduct(@RequestBody ProductRequestDTO productRequestDTO){
        return productService.createProduct(productRequestDTO);
    }

    @PostMapping("/update/{productId}")
    public ResponseEntity<ResponseData> updateProduct(@PathVariable Long productId, @RequestBody ProductRequestDTO productRequestDTO){
        return productService.updateProduct(productId, productRequestDTO);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ResponseData> deleteProduct(@PathVariable Long productId){
        return productService.deleteProduct(productId);
    }

    @GetMapping("/detail/{productId}")
    public ResponseEntity<ResponseData> getProductDetail(@PathVariable Long productId){
        return productService.getProductDetail(productId);
    }

    @PostMapping("/active/{productId}")
    public ResponseEntity<ResponseData> activeProduct(@PathVariable Long productId){
        return productService.activeProduct(productId);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> searchProduct(@RequestParam String q, int page){
        return productService.searchProduct(q, page);
    }

    @GetMapping("/find-by-category/{categoryId}")
    public ResponseEntity<ResponseData> getProductByCategory(@PathVariable Long categoryId, @RequestParam int page){
        return productService.getProductByCategory(categoryId, page);
    }
}
