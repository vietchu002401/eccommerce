package com.vti.ecommerce.service;

import com.vti.ecommerce.exception.ConflictException;
import com.vti.ecommerce.exception.NotFoundException;
import com.vti.ecommerce.exception.ServerErrorException;
import com.vti.ecommerce.model.UserPayment;
import com.vti.ecommerce.repository.UserPaymentRepository;
import com.vti.ecommerce.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserPaymentService {
    @Autowired
    private UserPaymentRepository userPaymentRepository;


    public ResponseEntity<ResponseData> createUserPayment(UserPayment userPayment) {
        try {
            if (userPaymentRepository.existsByUserId(userPayment.getUserId())) {
                throw new ConflictException("User is already have payment");
            }
            userPayment.setCreatedDate(new Date());
            userPayment.setUpdatedDate(new Date());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created user payment", userPaymentRepository.save(userPayment)));
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> updateUserPayment(UserPayment userPayment) {
        try {
            UserPayment u = userPaymentRepository.findById(userPayment.getId()).orElseThrow(() -> new NotFoundException("User payment not found"));
            u.setNumberCart(userPayment.getNumberCart());
            u.setProvider(userPayment.getProvider());
            u.setUpdatedDate(new Date());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Updated user payment", userPaymentRepository.save(u)));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> deleteUserPayment(Long userPaymentId) {
        try {
            userPaymentRepository.deleteById(userPaymentId);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Deleted", null));
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> getUserPaymentDetail(Long userPaymentId) {
        try {
            UserPayment userPayment = userPaymentRepository.findById(userPaymentId).orElseThrow(() -> new NotFoundException("User payment not found"));
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", userPayment));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }
}
