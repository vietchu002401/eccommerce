package com.vti.ecommerce.dto;

import com.vti.ecommerce.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private int amount;
    private Long categoryId;
    private boolean status;
    private List<ProductImage> productImages;
    private Date createdDate;
    private Date updatedDate;
}
