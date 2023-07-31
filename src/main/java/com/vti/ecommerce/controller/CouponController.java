package com.vti.ecommerce.controller;

import com.vti.ecommerce.dto.CouponDTO;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @GetMapping("/all")
    public ResponseEntity<ResponseData> getAllCoupon(@RequestParam int page) {
        return couponService.getAllCoupon(page);
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseData> createCoupon(@RequestBody CouponDTO couponDTO) {
        return couponService.createCoupon(couponDTO);
    }

    @PostMapping("/update/{couponId}")
    public ResponseEntity<ResponseData> updateCoupon(@PathVariable Long couponId, @RequestBody CouponDTO couponDTO) {
        return couponService.updateCoupon(couponId, couponDTO);
    }

    @PostMapping("/delete/{couponId}")
    public ResponseEntity<ResponseData> deleteCoupon(@PathVariable Long couponId) {
        return couponService.deleteCoupon(couponId);
    }
}
