package com.vti.ecommerce.auth;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NonNull
    @Size(min = 4, message = "At least 4 characters required")
    private String username;
    @NonNull
    @Size(min = 6, message = "At least 6 characters required")
    private String password;
    private boolean status;
    private String address;
    private String phone;
    private String email;
    private String role;
}
