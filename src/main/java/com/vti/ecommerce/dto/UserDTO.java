package com.vti.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private boolean status;
    private String address;
    private String phone;
    private String email;
    private Date createdDate;
    private Date updatedDate;
}
