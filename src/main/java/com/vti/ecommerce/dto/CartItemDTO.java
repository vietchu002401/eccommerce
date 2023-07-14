package com.vti.ecommerce.dto;

import com.vti.ecommerce.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long id;

    private Product product;

    private Long cartId;

    private int quantity;

    private Double subTotal;

    private Date createdDate;

    private Date updatedDate;
}
