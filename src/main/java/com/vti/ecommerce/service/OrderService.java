package com.vti.ecommerce.service;

import com.vti.ecommerce.config.JwtService;
import com.vti.ecommerce.dto.CartDTO;
import com.vti.ecommerce.dto.OrderDTO;
import com.vti.ecommerce.exception.BadRequestException;
import com.vti.ecommerce.exception.NotFoundException;
import com.vti.ecommerce.model.*;
import com.vti.ecommerce.repository.*;
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

    private List<OrderDTO> convertToOrderDTO(List<Order> orders, List<UserPayment> userPayments) {
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : orders) {
            for (UserPayment userPayment : userPayments) {
                if (order.getUserPaymentId().equals(userPayment.getId())) {
                    List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
                    OrderDTO orderDTO = OrderDTO.builder()
                            .id(order.getId())
                            .userId(order.getUserId())
                            .userPayment(userPayment)
                            .orderItemList(orderItems)
                            .totalPrice(order.getTotalPrice())
                            .statusShipping(order.isStatusShipping())
                            .createdDate(order.getCreatedDate())
                            .updatedDate(order.getUpdatedDate())
                            .build();
                    orderDTOS.add(orderDTO);
                    break;
                }
            }
        }
        return orderDTOS;
    }

    public ResponseEntity<ResponseData> createOrder(CartDTO cartDTO, String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
            UserPayment userPayment = userPaymentRepository.findByUserId(user.getId()).orElseThrow(() -> new NotFoundException("User payment not found"));
            List<CartItem> cartItemList = cartItemRepository.findCartItemByIdIn(cartDTO.getCartItemList());
            if (cartItemList.isEmpty()) {
                throw new NotFoundException("No item found");
            }
            for (CartItem cartItem : cartItemList) {
                Optional<Product> product = productRepository.findWhereAmountNotEnough(cartItem.getProductId(), cartItem.getQuantity());
                if (product.isPresent()) {
                    throw new BadRequestException(product.get().getName() + " has only " + product.get().getAmount() + " items left");
                }
            }
            Order order = Order.builder()
                    .userId(user.getId())
                    .userPaymentId(userPayment.getId())
                    .statusShipping(true)
                    .createdDate(new Date())
                    .updatedDate(new Date())
                    .build();
            Order newOrder = orderRepository.save(order);
            Double totalPrice = 0.0;
            for (CartItem cartItem : cartItemList) {
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
                //Chưa làm trừ amount ở trong product sau khi order
                totalPrice += orderItem.getSubTotal();
            }
            newOrder.setTotalPrice(totalPrice);
            orderRepository.save(newOrder);
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(newOrder.getId());
            OrderDTO orderDTO = OrderDTO.builder()
                    .id(newOrder.getId())
                    .userId(newOrder.getUserId())
                    .userPayment(userPayment)
                    .orderItemList(orderItems)
                    .statusShipping(newOrder.isStatusShipping())
                    .createdDate(newOrder.getCreatedDate())
                    .updatedDate(newOrder.getUpdatedDate())
                    .build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Order successfully", orderDTO));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getAllOrder() {
        try {
            List<Order> orders = orderRepository.findAll();
            List<UserPayment> userPayments = userPaymentRepository.findAll();
            List<OrderDTO> orderDTOS = convertToOrderDTO(orders, userPayments);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", orderDTOS));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getOrderDetail(Long orderId) {
        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Order not found", null));
            }
            Order order = orderOptional.get();
            Optional<UserPayment> userPayment = userPaymentRepository.findById(order.getUserPaymentId());
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
            OrderDTO orderDTO = OrderDTO.builder()
                    .id(order.getId())
                    .userId(order.getUserId())
                    .userPayment(userPayment.get())
                    .statusShipping(order.isStatusShipping())
                    .totalPrice(order.getTotalPrice())
                    .orderItemList(orderItems)
                    .createdDate(order.getCreatedDate())
                    .updatedDate(order.getUpdatedDate())
                    .build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", orderDTO));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getUserOrder(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            String username = jwtService.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "user not found", null));
            }
            User user = userOptional.get();
            List<Order> orders = orderRepository.findByUserId(user.getId());
//            List<UserPayment> userPayments = userPaymentRepository.findAll();
//            List<OrderDTO> orderDTOS = convertToOrderDTO(orders, userPayments);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", orders));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }
}
