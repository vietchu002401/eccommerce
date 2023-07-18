package com.vti.ecommerce.customerController;

import com.vti.ecommerce.model.CartItem;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.service.CartService;
import com.vti.ecommerce.service.CustomerService;
import com.vti.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class CustomerController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private CustomerService customerService;


    @GetMapping("/info")
    public ResponseEntity<ResponseData> getUserInfo(HttpServletRequest request){
        return customerService.getUserInfo(request);
    }

    @GetMapping("/order/all")
    public ResponseEntity<ResponseData> getUserOrder(HttpServletRequest request){
        return orderService.getUserOrder(request);
    }

    @GetMapping("/order/detail/{orderId}")
    public ResponseEntity<ResponseData> getOrderDetail(@PathVariable Long orderId){
        return orderService.getOrderDetail(orderId);
    }

    @DeleteMapping("/cart/remove-product/{cartItemId}")
    public ResponseEntity<ResponseData> deleteCartItem(@PathVariable Long cartItemId){
        return cartService.deleteCartItem(cartItemId);
    }

    @GetMapping("/cart/product-list/{cartId}")
    public ResponseEntity<ResponseData> getCartItem(@PathVariable Long cartId){
        return cartService.getCartItem(cartId);
    }

    @PostMapping("/add-quantity")
    public ResponseEntity<ResponseData> addQuantityToCart(@RequestParam String cartItemId, String quantity){
        return cartService.updateQuantity(cartItemId, quantity);
    }

    @PostMapping("/order")
    public ResponseEntity<ResponseData> createOrder(@RequestBody List<Long> cartItemId, HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        return orderService.createOrder(cartItemId, token);
    }
}
