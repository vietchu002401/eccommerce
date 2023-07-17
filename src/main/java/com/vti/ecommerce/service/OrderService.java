package com.vti.ecommerce.service;

import com.vti.ecommerce.config.JwtService;
import com.vti.ecommerce.dto.OrderDTO;
import com.vti.ecommerce.model.CartItem;
import com.vti.ecommerce.model.Order;
import com.vti.ecommerce.model.OrderItem;
import com.vti.ecommerce.model.Product;
import com.vti.ecommerce.model.UserPayment;
import com.vti.ecommerce.repository.CartItemRepository;
import com.vti.ecommerce.repository.OrderItemRepository;
import com.vti.ecommerce.repository.OrderRepository;
import com.vti.ecommerce.repository.ProductRepository;
import com.vti.ecommerce.repository.UserPaymentRepository;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.user.User;
import com.vti.ecommerce.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserPaymentRepository userPaymentRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;

    private static List<OrderDTO> convertToOrderDTO(List<Order> orders, List<UserPayment> userPayments){
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for(Order order : orders){
            for(UserPayment userPayment : userPayments){
                if(order.getUserPaymentId().equals(userPayment.getId())){
                    OrderDTO orderDTO = OrderDTO.builder()
                        .id(order.getId())
                        .userId(order.getUserId())
                        .userPayment(userPayment)
                        .status(order.isStatus())
                        .createdDate(order.getCreatedDate())
                        .updatedDate(order.getUpdatedDate())
                        .build();
                    orderDTOS.add(orderDTO);
                }
                break;
            }
        }
        return orderDTOS;
    }

    public ResponseEntity<ResponseData> createOrder(List<Long> cartItemId, String token){
        try{
            String username = jwtService.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUsername(username);
            if(userOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "User not found", null));
            }
            User user = userOptional.get();
            Optional<UserPayment> userPaymentOptional = userPaymentRepository.findByUserId(user.getId());
            if(userPaymentOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "User payment not found", null));
            }
            List<CartItem> cartItemList = cartItemRepository.findCartItemByIdIn(cartItemId);
            if(cartItemList.size() == 0){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "No item found", null));
            }
            for(CartItem cartItem : cartItemList){
                Optional<Product> product = productRepository.findWhereAmountNotEnough(cartItem.getProductId(), cartItem.getQuantity());
                if(product.isPresent()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseData(HttpStatus.BAD_REQUEST, product.get().getName() + " has only " + product.get().getAmount() + " items left", null));
                }
            }
            Order order = Order.builder()
                .userId(user.getId())
                .userPaymentId(userPaymentOptional.get().getId())
                .status(true)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
            Order newOrder = orderRepository.save(order);
            Double totalPrice = (double) 0;
            for(CartItem cartItem : cartItemList){
                OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .orderId(newOrder.getId())
                    .quantity(cartItem.getQuantity())
                    .subTotal(cartItem.getSubTotal())
                    .createdDate(new Date())
                    .updatedDate(new Date())
                    .build();
                orderItemRepository.save(orderItem);
                cartItemRepository.deleteById(cartItem.getId());
                totalPrice+=orderItem.getSubTotal();
            }
            newOrder.setTotalPrice(totalPrice);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Order successfully", orderRepository.save(newOrder)));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getAllOrder() {
        try{
            List<Order> orders = orderRepository.findAll();
            List<UserPayment> userPayments = userPaymentRepository.findAll();
            List<OrderDTO> orderDTOS = convertToOrderDTO(orders, userPayments);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", orderDTOS));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getOrderDetail(Long orderId) {
        try{
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if(orderOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Order not found", null));
            }
            Order order = orderOptional.get();
            Optional<UserPayment> userPayment = userPaymentRepository.findById(order.getUserPaymentId());
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
            OrderDTO orderDTO = OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .userPayment(userPayment.get())
                .status(order.isStatus())
                .totalPrice(order.getTotalPrice())
                .items(orderItems)
                .createdDate(order.getCreatedDate())
                .updatedDate(order.getUpdatedDate())
                .build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", orderDTO));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getUserOrder(HttpServletRequest request) {
        try{
            String token = request.getHeader("Authorization").substring(7);
            String username = jwtService.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUsername(username);
            if(userOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "user not found", null));
            }
            User user = userOptional.get();
            List<Order> orders = orderRepository.findByUserId(user.getId());
            List<UserPayment> userPayments = userPaymentRepository.findAll();
            List<OrderDTO> orderDTOS = convertToOrderDTO(orders, userPayments);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", orderDTOS));
        }catch (Exception e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }
}
