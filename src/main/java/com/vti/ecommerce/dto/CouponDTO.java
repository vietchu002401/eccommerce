package com.vti.ecommerce.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private Integer discountPercent;

    @NotNull
    private Integer maxUsage;

    @NotNull
    private String expirationDate;

    @NotNull
    private Double minTotalPrice;

    private boolean status;

    private String couponType;

    private Date createdDate;

    private Date updatedDate;
}
