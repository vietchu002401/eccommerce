package com.vti.ecommerce.service;

import com.vti.ecommerce.exception.ConflictException;
import com.vti.ecommerce.exception.NotFoundException;
import com.vti.ecommerce.model.Coupon;
import com.vti.ecommerce.repository.CouponRepository;
import com.vti.ecommerce.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

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
        try{
            Pageable pageable = PageRequest.of(page, 8);
            List<Coupon> coupons = couponRepository.findAllWithPage(pageable);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", coupons));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> createCoupon(Coupon coupon) throws ServerErrorException, ParseException {
        if(couponRepository.existsByCode(coupon.getCode())){
            throw new ConflictException("Coupon is already exist");
        }
        String expirated = convertDateFormat(coupon.getExpirationDate());
        coupon.setExpirationDate(expirated);
        coupon.setCreatedDate(new Date());
        coupon.setUpdatedDate(new Date());
        coupon.setStatus(true);
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created", couponRepository.save(coupon)));
    }

    public ResponseEntity<ResponseData> checkCoupon(String couponCode) {
        try{
            Coupon coupon = couponRepository.findByCode(couponCode).orElseThrow(()-> new NotFoundException("Coupon not found"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date expirationDate = sdf.parse(coupon.getExpirationDate());
            if(expirationDate.before(new Date())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, "Coupon is expirated", couponCode));
            }
            if(coupon.getMaxUsage() < 1){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, "Coupon out of limit", couponCode));
            }
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Coupon valid", coupon));
        }catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null));
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, e.getMessage(), null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> updateCoupon(Long couponId, Coupon coupon) {
        try{
            Coupon c = couponRepository.findById(couponId).orElseThrow(()-> new NotFoundException("Coupon not found"));
            if(!c.getCode().equals(coupon.getCode()) &&  couponRepository.existsByCode(coupon.getCode())){
                throw new ConflictException("This code is already exist");
            }
            coupon.setId(c.getId());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Updated", couponRepository.save(coupon)));
        }catch (ConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, e.getMessage(), null));
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, e.getMessage(), null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    public ResponseEntity<ResponseData> deleteCoupon(Long couponId) {
        try{
          couponRepository.deleteById(couponId);
          return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Deleted", null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }
}
