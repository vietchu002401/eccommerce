package com.vti.ecommerce.dto;

import com.vti.ecommerce.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private int amount;
    private Category category;
    private boolean status;
    private Date createdDate;
    private Date updatedDate;
}
