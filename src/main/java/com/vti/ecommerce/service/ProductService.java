package com.vti.ecommerce.service;

import com.vti.ecommerce.dto.ProductDTO;
import com.vti.ecommerce.model.Category;
import com.vti.ecommerce.model.Product;
import com.vti.ecommerce.repository.CategoryRepository;
import com.vti.ecommerce.repository.OrderItemRepository;
import com.vti.ecommerce.repository.ProductRepository;
import com.vti.ecommerce.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    private static List<ProductDTO> convertToProductDTO(List<Product> products, List<Category> categories){
        List<ProductDTO> productDTOS = new ArrayList<>();
        for (Product product : products) {
            for (Category category : categories) {
                if (product.getCategoryId() == category.getId()) {
                    ProductDTO p = ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .amount(product.getAmount())
                        .category(category)
                        .status(product.isStatus())
                        .createdDate(product.getCreatedDate())
                        .updatedDate(product.getUpdatedDate())
                        .build();
                    productDTOS.add(p);
                    break;
                }
            }
        }
        return productDTOS;
    }

    public ResponseEntity<ResponseData> getAllProduct() {
        try {
            List<Product> products = productRepository.findAll();
            List<Category> categories = categoryRepository.findAll();
            List<ProductDTO> productDTOS = convertToProductDTO(products, categories);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", productDTOS));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> createProduct(Product product) {
        try {
            if (productRepository.existsByName(product.getName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            product.setUpdatedDate(new Date());
            product.setCreatedDate(new Date());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created new product", productRepository.save(product)));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> updateProduct(Long productId, Product productRequest) {
        try{
            Optional<Product> productOptional = productRepository.findById(productId);
            if(productOptional.isPresent()){
                Product product = productOptional.get();
                if(!product.getName().equals(productRequest.getName())){
                    if (productRepository.existsByName(productRequest.getName())) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, "This name is already exist", productRequest));
                    }
                }
                product.setName(productRequest.getName());
                product.setPrice(productRequest.getPrice());
                product.setDescription(productRequest.getDescription());
                product.setAmount(productRequest.getAmount());
                product.setCategoryId(productRequest.getCategoryId());
                product.setStatus(productRequest.isStatus());
                product.setUpdatedDate(new Date());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, "Updated", productRepository.save(product)));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> deleteProduct(Long productId) {
        try{
            Optional<Product> productOptional = productRepository.findById(productId);
            if(productOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null));
            }
            productRepository.deleteById(productId);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Deleted", productOptional.get()));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> getProductDetail(Long productId) {
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                Optional<Category> category = categoryRepository.findById(product.getCategoryId());
                ProductDTO productDTO = ProductDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .amount(product.getAmount())
                    .category(category.get())
                    .status(product.isStatus())
                    .createdDate(product.getCreatedDate())
                    .updatedDate(product.getUpdatedDate())
                    .build();
                return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", productDTO));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> searchProduct(String q) {
        try{
            List<Product> products = productRepository.searchProductByKeyword(q);
            List<Category> categories = categoryRepository.findAll();
            List<ProductDTO> productDTOS = convertToProductDTO(products, categories);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", productDTOS));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> getProductByCategory(Long categoryId) {
        try{
           List<Product> products = productRepository.findAllByCategoryId(categoryId);
           List<Category> categories = categoryRepository.findAll();
           List<ProductDTO> productDTOS = convertToProductDTO(products, categories);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", productDTOS));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> getBestSeller() {
        try{
            List<Product> products = productRepository.findBestSeller();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", products));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }
}
