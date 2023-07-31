package com.vti.ecommerce.service;

import com.vti.ecommerce.dto.CouponDTO;
import com.vti.ecommerce.exception.ConflictException;
import com.vti.ecommerce.exception.NotFoundException;
import com.vti.ecommerce.exception.ServerErrorException;
import com.vti.ecommerce.model.Coupon;
import com.vti.ecommerce.model.CouponType;
import com.vti.ecommerce.repository.CouponRepository;
import com.vti.ecommerce.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    private static String convertDateFormat(String dateString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (z)");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        // Parse chuỗi ban đầu thành đối tượng Date
        Date date = inputFormat.parse(dateString);

        // Chuyển đổi múi giờ thành múi giờ UTC
        TimeZone indochinaTimeZone = TimeZone.getTimeZone("GMT+7");
        outputFormat.setTimeZone(indochinaTimeZone);

        // Tạo chuỗi định dạng mới
        return outputFormat.format(date);
    }

    public ResponseEntity<ResponseData> getAllCoupon(int page) {
        try {
            Pageable pageable = PageRequest.of(page, 8);
            List<Coupon> coupons = couponRepository.findAllWithPage(pageable);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", coupons));
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> createCoupon(CouponDTO couponDTO) {
        try {
            if (couponRepository.existsByCode(couponDTO.getCode())) {
                throw new ConflictException("Coupon is already exist");
            }
            String expirated = convertDateFormat(couponDTO.getExpirationDate());
            String type = "";
            if (couponDTO.getCouponType() != null) {
                type = couponDTO.getCouponType().toUpperCase();
            }
            Coupon coupon = Coupon.builder()
                .code(couponDTO.getCode())
                .discountPercent(couponDTO.getDiscountPercent())
                .maxUsage(couponDTO.getMaxUsage())
                .expirationDate(expirated)
                .minTotalPrice(couponDTO.getMinTotalPrice())
                .status(true)
                .couponType(type.equals("PERCENT") ? CouponType.PERCENT : CouponType.FIXED)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created", couponRepository.save(coupon)));
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> checkCoupon(String couponCode) {
        try {
            Coupon coupon = couponRepository.findByCode(couponCode).orElseThrow(() -> new NotFoundException("Coupon not found"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date expirationDate = sdf.parse(coupon.getExpirationDate());
            if (expirationDate.before(new Date()) || !coupon.isStatus()) {
                throw new ConflictException("Coupon is expirated");
            }
            if (coupon.getMaxUsage() < 1) {
                throw new ConflictException("Coupon out of limit");
            }
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Coupon valid", coupon));
        } catch (NotFoundException | ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> updateCoupon(Long couponId, CouponDTO couponDTO) {
        try {
            Coupon c = couponRepository.findById(couponId).orElseThrow(() -> new NotFoundException("Coupon not found"));
            if (!c.getCode().equals(couponDTO.getCode()) && couponRepository.existsByCode(couponDTO.getCode())) {
                throw new ConflictException("This code is already exist");
            }
            String expirated = convertDateFormat(couponDTO.getExpirationDate());
            String type = "";
            if (couponDTO.getCouponType() != null) {
                type = couponDTO.getCouponType().toUpperCase();
            }
            Coupon coupon = Coupon.builder()
                .id(couponId)
                .code(couponDTO.getCode())
                .discountPercent(couponDTO.getDiscountPercent())
                .maxUsage(couponDTO.getMaxUsage())
                .expirationDate(expirated)
                .minTotalPrice(couponDTO.getMinTotalPrice())
                .status(couponDTO.isStatus())
                .couponType(type.equals("PERCENT") ? CouponType.PERCENT : CouponType.FIXED)
                .createdDate(c.getCreatedDate())
                .updatedDate(new Date())
                .build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Updated", couponRepository.save(coupon)));
        } catch (ConflictException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> deleteCoupon(Long couponId) {
        try {
            couponRepository.deleteById(couponId);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Deleted", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }
}
