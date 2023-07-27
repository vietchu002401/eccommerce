package com.vti.ecommerce.customerController;

import com.vti.ecommerce.dto.CartDTO;
import com.vti.ecommerce.dto.UserDTO;
import com.vti.ecommerce.model.CartItem;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.CartService;
import com.vti.ecommerce.service.CouponService;
import com.vti.ecommerce.service.CustomerService;
import com.vti.ecommerce.service.OrderService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/user")
public class CustomerController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CouponService couponService;

    @GetMapping("/info")
    public ResponseEntity<ResponseData> getUserInfo(HttpServletRequest request){
        return customerService.getUserInfo(request);
    }

    @PostMapping("/info/edit")
    public ResponseEntity<ResponseData> editProfile(UserDTO userDTO){
        return customerService.editProfile(userDTO);
    }

    @GetMapping("/order/all")
    public ResponseEntity<ResponseData> getUserOrder(HttpServletRequest request, @RequestParam int page){
        return orderService.getUserOrder(request, page);
    }

    @GetMapping("/order/detail/{orderId}")
    public ResponseEntity<ResponseData> getOrderDetail(@PathVariable Long orderId){
        return orderService.getOrderDetail(orderId);
    }

    @PostMapping("/cart/remove-product/{cartItemId}")
    public ResponseEntity<ResponseData> deleteCartItem(@PathVariable Long cartItemId, HttpServletRequest httpServletRequest){
        return cartService.deleteCartItem(cartItemId, httpServletRequest);
    }

    @GetMapping("/cart/product-list")
    public ResponseEntity<ResponseData> getCartItem(HttpServletRequest httpServletRequest, @RequestParam int page){
        return cartService.getCartItem(httpServletRequest, page);
    }

    @PostMapping("/cart/update-quantity")
    public ResponseEntity<ResponseData> addQuantityToCart(@RequestBody CartItem cartItem){
        return cartService.updateQuantity(cartItem);
    }

    @PostMapping("/cart/order")
    public ResponseEntity<ResponseData> createOrder(@RequestBody CartDTO cartDTO, HttpServletRequest request) throws MessagingException, IOException, ParseException {
        String token = request.getHeader("Authorization").substring(7);
        return orderService.createOrder(cartDTO, token);
    }

    @PostMapping("/coupon/check/{couponCode}")
    public ResponseEntity<ResponseData> checkCoupon(@PathVariable String couponCode){
        return couponService.checkCoupon(couponCode);
    }
}
