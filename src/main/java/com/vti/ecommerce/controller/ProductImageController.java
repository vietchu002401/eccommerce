package com.vti.ecommerce.controller;

import com.vti.ecommerce.model.ProductImage;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.ProductImageService;
import jakarta.annotation.security.RolesAllowed;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product-image")
public class ProductImageController {
    @Autowired
    private ProductImageService productImageService;

    @PostMapping("/add")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<ResponseData> createProductImage(@RequestParam Long productId, @RequestParam List<MultipartFile> files){
        return productImageService.createProductImage(productId, files);
    }

    @GetMapping("/product-image-list/{productId}")
    public ResponseEntity<ResponseData> getProductImage(@PathVariable Long productId, @RequestParam int page){
        return productImageService.getProductImage(productId, page);
    }

    @PostMapping("/update/{productImageId}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<ResponseData> updateProductImage(@PathVariable Long productImageId, @RequestBody ProductImage productImage){
        return productImageService.updateProductImage(productImageId, productImage);
    }

    @DeleteMapping("/delete/{productImageId}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<ResponseData> deleteProductImage(@PathVariable Long productImageId){
        return productImageService.deleteProductImage(productImageId);
    }
}
