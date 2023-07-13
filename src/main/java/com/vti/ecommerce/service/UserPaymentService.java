package com.vti.ecommerce.service;

import com.vti.ecommerce.model.UserPayment;
import com.vti.ecommerce.repository.UserPaymentRepository;
import com.vti.ecommerce.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserPaymentService {
    @Autowired
    private UserPaymentRepository userPaymentRepository;


    public ResponseEntity<ResponseData> createUserPayment(UserPayment userPayment) {
        try{
            if(userPaymentRepository.existsByUserId(userPayment.getUserId())){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            userPayment.setCreatedDate(new Date());
            userPayment.setUpdatedDate(new Date());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created user payment", userPaymentRepository.save(userPayment)));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> updateUserPayment(UserPayment userPayment) {
        try{
            Optional<UserPayment> userPaymentOptional = userPaymentRepository.findById(userPayment.getId());
            if(userPaymentOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "User payment not found", null));
            }
            UserPayment u = userPaymentOptional.get();
            u.setPaymentType(userPayment.getPaymentType());
            u.setProvider(userPayment.getProvider());
            u.setUpdatedDate(new Date());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Updated user payment", userPaymentRepository.save(u)));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }
}
