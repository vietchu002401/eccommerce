package com.vti.ecommerce.service;

import com.vti.ecommerce.model.ProductImage;
import com.vti.ecommerce.repository.ProductImageRepository;
import com.vti.ecommerce.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductImageService {
    @Autowired
    private ProductImageRepository productImageRepository;


    public ResponseEntity<ResponseData> createProductImage(ProductImage productImage) {
        try{
            if(productImageRepository.existsBySourceImage(productImage.getSourceImage())){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            productImage.setCreatedDate(new Date());
            productImage.setUpdatedDate(new Date());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created product image", productImageRepository.save(productImage)));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getProductImage(Long productId, int page) {
        try{
            Pageable pageable = PageRequest.of(page, 8);
            List<ProductImage> productImages = productImageRepository.findByProductIdPage(productId, pageable);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", productImages));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> updateProductImage(Long productImageId, ProductImage productImage) {
        try{
            Optional<ProductImage> productImageOptional = productImageRepository.findById(productImageId);
            if(productImageOptional.isPresent()){
                ProductImage p = productImageOptional.get();
                if(p.getSourceImage().equals(productImage.getSourceImage())){
                    if(productImageRepository.existsBySourceImage(productImage.getSourceImage())){
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, "Url is already exist", productImage));
                    }
                }
                p.setSourceImage(productImage.getSourceImage());
                p.setProductId(productImage.getProductId());
                p.setUpdatedDate(new Date());
                return ResponseEntity.ok(new ResponseData(HttpStatus.CONFLICT, "Updated",productImageRepository.save(p)));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> deleteProductImage(Long productImageId) {
        try{
            if(!productImageRepository.existsById(productImageId)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Image id not found", productImageId));
            }
            productImageRepository.deleteById(productImageId);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Deleted", productImageId));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }
}
