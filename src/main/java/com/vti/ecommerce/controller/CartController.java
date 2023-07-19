package com.vti.ecommerce.controller;

import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<ResponseData> createCart(HttpServletRequest httpServletRequest){
        return cartService.createCart(httpServletRequest);
    }

    @GetMapping("/get-by-user-id/{userId}")
    public ResponseEntity<ResponseData> getUserCart(@PathVariable Long userId){
        return cartService.getUserCart(userId);
    }
}
