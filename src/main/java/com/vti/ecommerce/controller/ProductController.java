package com.vti.ecommerce.controller;

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
    public ResponseEntity<ResponseData> getALlProduct(){
        return productService.getAllProduct();
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseData> createProduct(@RequestBody Product product){
        return productService.createProduct(product);
    }

    @PostMapping("/update/{productId}")
    public ResponseEntity<ResponseData> updateProduct(@PathVariable Long productId, @RequestBody Product productRequest){
        return productService.updateProduct(productId, productRequest);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ResponseData> deleteProduct(@PathVariable Long productId){
        return productService.deleteProduct(productId);
    }

    @GetMapping("/detail/{productId}")
    public ResponseEntity<ResponseData> getProductDetail(@PathVariable Long productId){
        return productService.getProductDetail(productId);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> searchProduct(@RequestParam String q){
        return productService.searchProduct(q);
    }

    @GetMapping("/product-list-by-category/{categoryId}")
    public ResponseEntity<ResponseData> getProductByCategory(@PathVariable Long categoryId){
        return productService.getProductByCategory(categoryId);
    }
}
