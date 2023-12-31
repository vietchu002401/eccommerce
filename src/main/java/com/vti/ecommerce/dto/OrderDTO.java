package com.vti.ecommerce.dto;

import com.vti.ecommerce.model.OrderItem;
import com.vti.ecommerce.model.UserPayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;

    private Long userId;

    private UserPayment userPayment;

    private boolean statusShipping;

    private Double totalPrice;

    private List<OrderItem> orderItemList;

    private Date createdDate;

    private Date updatedDate;
}
