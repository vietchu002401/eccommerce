package com.vti.ecommerce.controller;

import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<ResponseData> getAllOrder(@RequestParam int page){
        return orderService.getAllOrder(page);
    }

    @GetMapping("/detail/{orderId}")
    public ResponseEntity<ResponseData> getOrderDetail(@PathVariable Long orderId){
        return orderService.getOrderDetail(orderId);
    }
}
