package com.vti.ecommerce.controller;

import com.vti.ecommerce.model.UserPayment;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.UserPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/payment")
public class UserPaymentController {
    @Autowired
    private UserPaymentService userPaymentService;

    @PostMapping("/add")
    public ResponseEntity<ResponseData> createUserPayment(@RequestBody UserPayment userPayment){
        return userPaymentService.createUserPayment(userPayment);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseData> updateUserPayment(@RequestBody UserPayment userPayment){
        return userPaymentService.updateUserPayment(userPayment);
    }

    @PostMapping("/delete/{userPaymentId}")
    public ResponseEntity<ResponseData> deleteUserPayment(@PathVariable Long userPaymentId){
        return userPaymentService.deleteUserPayment(userPaymentId);
    }

    @GetMapping("/detail/{userPaymentId}")
    public ResponseEntity<ResponseData> getUserPaymentDetail(@PathVariable Long userPaymentId){
        return userPaymentService.getUserPaymentDetail(userPaymentId);
    }
}
