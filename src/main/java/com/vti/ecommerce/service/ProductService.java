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
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private FileService fileService;

    private void createCell(Row row, int columnCount, Object value, CellStyle style, XSSFSheet sheet) {
        Cell cell = row.createCell(columnCount);
        sheet.autoSizeColumn(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue(String.valueOf(value));
        }
        cell.setCellStyle(style);
    }

    private List<ProductDTO> convertToProductDTO(List<Product> products, List<Category> categories) {
        List<ProductDTO> productDTOS = new ArrayList<>();
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category category : categories) {
            categoryMap.put(category.getId(), category);
        }
        for (Product product : products) {
            List<ProductImage> productImages = productImageRepository.findAllByProductId(product.getId());
            ProductDTO p = ProductDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .amount(product.getAmount())
                    .category(categoryMap.get(product.getCategoryId()))
                    .status(product.isStatus())
                    .productImages(productImages)
                    .createdDate(product.getCreatedDate())
                    .updatedDate(product.getUpdatedDate())
                    .build();
            productDTOS.add(p);
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

    public ResponseEntity<ResponseData> createProduct(ProductRequestDTO productRequestDTO, List<MultipartFile> files) throws ServerErrorException {
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
        if (!files.isEmpty()) {
            for (MultipartFile file : files) {
                String pathImage = fileService.save(file);
                ProductImage productImage = ProductImage.builder()
                        .productId(productSaved.getId())
                        .sourceImage(pathImage)
                        .status(true)
                        .createdDate(new Date())
                        .updatedDate(new Date())
                        .build();
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
        PageRequest pageRequest = PageRequest.of(page, 8);
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
        List<Category> categories = categoryRepository.findAll();
        List<ProductDTO> productDTOS = convertToProductDTO(products, categories);
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", productDTOS));
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

    public ResponseEntity<ResponseData> downloadProduct(HttpServletResponse response) {
        try {
            Class<Product> productClass = Product.class;
            Field[] fields = productClass.getDeclaredFields();
            List<String> titles = new ArrayList<>();
            titles.add("STT");
            for (Field field : fields) {
                titles.add(field.getName());
            }
            List<Product> productList = productRepository.findAll();

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Products");

            //header
            Row rowHeader = sheet.createRow(0);
            CellStyle styleHeader = workbook.createCellStyle();
            XSSFFont fontHeader = workbook.createFont();
            fontHeader.setBold(true);
            fontHeader.setFontHeight(16);
            styleHeader.setFont(fontHeader);
            for (int i = 0; i < titles.size(); i++) {
                createCell(rowHeader, i, titles.get(i), styleHeader, sheet);
            }

            //write
            int rowCount = 1;
            CellStyle styleBody = workbook.createCellStyle();
            XSSFFont fontBody = workbook.createFont();
            fontBody.setFontHeight(14);
            styleBody.setFont(fontBody);
            int stt = 1;
            for (Product product : productList) {
                Row rowBody = sheet.createRow(rowCount++);
                int columnCount = 0;
                createCell(rowBody, columnCount++, stt++, styleBody, sheet);
                createCell(rowBody, columnCount++, product.getId(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getName(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getPrice(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getDescription(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getAmount(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getCategoryId(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.isStatus(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getCreatedDate(), styleBody, sheet);
                createCell(rowBody, columnCount, product.getUpdatedDate(), styleBody, sheet);
            }
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Dowloaded", null));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
