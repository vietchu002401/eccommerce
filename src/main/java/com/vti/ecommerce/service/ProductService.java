package com.vti.ecommerce.service;

import com.vti.ecommerce.dto.ProductDTO;
import com.vti.ecommerce.dto.ProductRequestDTO;
import com.vti.ecommerce.exception.ConflictException;
import com.vti.ecommerce.exception.NotFoundException;
import com.vti.ecommerce.model.Category;
import com.vti.ecommerce.model.Product;
import com.vti.ecommerce.model.ProductImage;
import com.vti.ecommerce.repository.CategoryRepository;
import com.vti.ecommerce.repository.ProductImageRepository;
import com.vti.ecommerce.repository.ProductRepository;
import com.vti.ecommerce.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

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
    private ProductImageRepository productImageRepository;

    private List<ProductDTO> convertToProductDTO(List<Product> products, List<Category> categories) {
        List<ProductDTO> productDTOS = new ArrayList<>();
        for (Product product : products) {
            for (Category category : categories) {
                if (product.getCategoryId() == category.getId()) {
                    List<ProductImage> productImages = productImageRepository.findAllByProductId(product.getId());
                    ProductDTO p = ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .amount(product.getAmount())
                        .category(category)
                        .status(product.isStatus())
                        .productImages(productImages)
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

    public ResponseEntity<ResponseData> getAllProduct(int page) {
        try {
            Pageable pageable = PageRequest.of(page, 8);
            List<Product> products = productRepository.findAllWithPage(pageable);
            List<Category> categories = categoryRepository.findAll();
            List<ProductDTO> productDTOS = convertToProductDTO(products, categories);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", productDTOS));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> createProduct(ProductRequestDTO productRequestDTO) throws ServerErrorException {
        if (productRepository.existsByName(productRequestDTO.getName())) {
            throw new ConflictException("Product name is already exist");
        }
        if (!categoryRepository.existsById(productRequestDTO.getCategoryId())) {
            throw new NotFoundException("Category not found");
        }
        Product product = Product.builder()
            .name(productRequestDTO.getName())
            .price(productRequestDTO.getPrice())
            .description(productRequestDTO.getDescription())
            .amount(productRequestDTO.getAmount())
            .categoryId(productRequestDTO.getCategoryId())
            .status(productRequestDTO.isStatus())
            .createdDate(new Date())
            .updatedDate(new Date())
            .build();
        Product productSaved = productRepository.save(product);
        if (productRequestDTO.getProductImages() != null) {
            for (ProductImage productImage : productRequestDTO.getProductImages()) {
                productImage.setProductId(productSaved.getId());
                productImage.setCreatedDate(new Date());
                productImage.setUpdatedDate(new Date());
                productImageRepository.save(productImage);
            }
        }
        List<Product> products = new ArrayList<>();
        List<Category> categories = categoryRepository.findAll();
        products.add(productSaved);
        List<ProductDTO> productDTOS = convertToProductDTO(products, categories);
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created new product", productDTOS));
    }

    public ResponseEntity<ResponseData> updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                if (!product.getName().equals(productRequestDTO.getName())) {
                    if (productRepository.existsByName(productRequestDTO.getName())) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, "This name is already exist", productRequestDTO));
                    }
                }
                Product productUpdate = Product.builder()
                    .id(product.getId())
                    .name(productRequestDTO.getName())
                    .price(productRequestDTO.getPrice())
                    .description(productRequestDTO.getDescription())
                    .amount(productRequestDTO.getAmount())
                    .categoryId(productRequestDTO.getCategoryId())
                    .status(productRequestDTO.isStatus())
                    .createdDate(product.getCreatedDate())
                    .updatedDate(new Date())
                    .build();
                Product productSaved = productRepository.save(productUpdate);
                for (ProductImage productImage : productRequestDTO.getProductImages()) {
                    if (productImage.getId() == null) {
                        productImage.setProductId(productSaved.getId());
                        productImage.setCreatedDate(new Date());
                        productImage.setUpdatedDate(new Date());
                        productImageRepository.save(productImage);
                        break;
                    }
                    Optional<ProductImage> productImageOptional = productImageRepository.findById(productImage.getId());
                    if (productImageOptional.isEmpty()) {
                        break;
                    }
                    ProductImage p = productImageOptional.get();
                    p.setProductId(p.getProductId());
                    p.setSourceImage(productImage.getSourceImage());
                    p.setStatus(productImage.isStatus());
                    p.setUpdatedDate(new Date());
                    productImageRepository.save(p);
                }
                List<Product> products = new ArrayList<>();
                List<Category> categories = categoryRepository.findAll();
                products.add(productSaved);
                List<ProductDTO> productDTOS = convertToProductDTO(products, categories);
                return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Updated", productDTOS));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> deleteProduct(Long productId) {
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null));
            }
            productRepository.deleteById(productId);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Deleted", productOptional.get()));
        } catch (Exception e) {
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
                List<ProductImage> productImages = productImageRepository.findAllByProductId(productId);
                ProductDTO productDTO = ProductDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .amount(product.getAmount())
                    .category(category.get())
                    .productImages(productImages)
                    .status(product.isStatus())
                    .createdDate(product.getCreatedDate())
                    .updatedDate(product.getUpdatedDate())
                    .build();
                return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", productDTO));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> searchProduct(String q, int page) throws ServerErrorException {
        PageRequest pageRequest =  PageRequest.of(page, 8);
        List<Product> products = productRepository.searchProductByKeyword(q, pageRequest);
        if (products.isEmpty()) {
            throw new NotFoundException("Product not found");
        }
        List<Category> categories = categoryRepository.findAll();
        List<ProductDTO> productDTOS = convertToProductDTO(products, categories);
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully " + productDTOS.size() + " items", productDTOS));
    }

    public ResponseEntity<ResponseData> getProductByCategory(Long categoryId, int page) {
        try {
            Pageable pageable = PageRequest.of(page, 8);
            List<Product> products = productRepository.findAllByCategoryId(categoryId, pageable);
            List<Category> categories = categoryRepository.findAll();
            List<ProductDTO> productDTOS = convertToProductDTO(products, categories);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", productDTOS));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> getBestSeller() throws ServerErrorException {
        List<Product> products = productRepository.findBestSeller();
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", products));
    }

    public ResponseEntity<ResponseData> activeProduct(Long productId) {
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null));
            }
            Product p = productOptional.get();
            p.setStatus(true);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Active successfully", productRepository.save(p)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }
}
