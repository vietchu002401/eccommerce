package com.vti.ecommerce.service;

import com.vti.ecommerce.config.JwtService;
import com.vti.ecommerce.dto.CartItemDTO;
import com.vti.ecommerce.model.Cart;
import com.vti.ecommerce.model.CartItem;
import com.vti.ecommerce.model.Product;
import com.vti.ecommerce.repository.CartItemRepository;
import com.vti.ecommerce.repository.CartRepository;
import com.vti.ecommerce.repository.ProductRepository;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.user.User;
import com.vti.ecommerce.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    private List<CartItemDTO> convertToCartItemDTO(List<CartItem> cartItemList) {
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            Optional<Product> productOptional = productRepository.findById(cartItem.getProductId());
            CartItemDTO cartItemDTO = CartItemDTO.builder()
                    .id(cartItem.getId())
                    .product(productOptional.isPresent() ? productOptional.get() : null)
                    .cartId(cartItem.getCartId())
                    .subTotal(cartItem.getSubTotal())
                    .quantity(cartItem.getQuantity())
                    .createdDate(cartItem.getCreatedDate())
                    .updatedDate(cartItem.getUpdatedDate())
                    .build();
            cartItemDTOS.add(cartItemDTO);
        }
        return cartItemDTOS;
    }

    public ResponseEntity<ResponseData> createCart(HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            String username = jwtService.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUsername(username);
            if(userOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "User not found", null));
            }
            User user = userOptional.get();
            if (cartRepository.existsByUserId(user.getId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, "This user is already have a cart", null));
            }
            if (!userRepository.existsById(user.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseData(HttpStatus.BAD_REQUEST, "This user not found", null));
            }
            Cart cart = Cart.builder()
                .userId(user.getId())
                .status(true)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created cart", cartRepository.save(cart)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> addToCart(Long productId, HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            String username = jwtService.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUsername(username);
            if(userOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "User not found", null));
            }
            if(!cartRepository.existsByUserId(userOptional.get().getId())){
                Cart cart = Cart.builder()
                    .userId(userOptional.get().getId())
                    .status(true)
                    .createdDate(new Date())
                    .updatedDate(new Date())
                    .build();
                cartRepository.save(cart);
            }
            Optional<Cart> cartOptional = cartRepository.findByUserId(userOptional.get().getId());
            if(cartOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Cart not found", null));
            }
            Long cartId = cartOptional.get().getId();
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null));
            }
            Product product = productOptional.get();
            Optional<CartItem> cartItemOptional = cartItemRepository.findByProductIdAndCartId(productId, cartId);
            if (cartItemOptional.isPresent()) {
                CartItem c = cartItemOptional.get();
                c.setQuantity(c.getQuantity() + 1);
                c.setSubTotal(c.getSubTotal() + product.getPrice());
                c.setUpdatedDate(new Date());
                if (product.getAmount() < c.getQuantity()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseData(HttpStatus.BAD_REQUEST, "The product has only " + product.getAmount() + " items left", null));
                }
                return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Add successfully", cartItemRepository.save(c)));
            }
            if (product.getAmount() < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseData(HttpStatus.BAD_REQUEST, "The product has only " + product.getAmount() + " items left", null));
            }
            CartItem cartItem = CartItem.builder()
                    .subTotal(product.getPrice())
                    .createdDate(new Date())
                    .updatedDate(new Date())
                    .productId(productId)
                    .status(true)
                    .quantity(1)
                    .cartId(cartId).build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Add successfully", cartItemRepository.save(cartItem)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> deleteCartItem(Long cartItemId, HttpServletRequest httpServletRequest) {
        try {
            Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
            if (cartItemOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Item not found", null));
            }
            cartItemRepository.deleteById(cartItemOptional.get().getId());
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            String username = jwtService.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUsername(username);
            if(userOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "User not found", null));
            }
            User user = userOptional.get();
            Optional<Cart> cartOptional = cartRepository.findByUserId(user.getId());
            if(cartOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Cart not found", null));
            }
            Cart cart = cartOptional.get();
            Pageable pageable = PageRequest.of(0, 10000);
            List<CartItem> cartItemList = cartItemRepository.findALlByCartId(cart.getId(), pageable);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Delete successfully", cartItemList));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getUserCart(Long userId) {
        try {
            Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
            if (cartOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Cart not found", null));
            }
            Cart cart = cartOptional.get();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", cart));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> getCartItem(HttpServletRequest httpServletRequest, int page) {
        try {
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            String username = jwtService.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUsername(username);
            if(userOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "User not found", null));
            }
            User user = userOptional.get();
            Optional<Cart> cartOptional = cartRepository.findByUserId(user.getId());
            if(cartOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Cart not found", null));
            }
            Cart cart = cartOptional.get();
            Pageable pageable = PageRequest.of(page, 8);
            List<CartItem> cartItemList = cartItemRepository.findALlByCartId(cart.getId(), pageable);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", cartItemList));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> updateQuantity(CartItem cartItem) {
        try {
            Optional<Product> productOptional = productRepository.findWhereAmountNotEnough(cartItem.getProductId(), cartItem.getQuantity());
            if(productOptional.isPresent()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseData(HttpStatus.BAD_REQUEST, productOptional.get().getName() + " has only " + productOptional.get().getAmount() + " items left!", null));
            }
            cartItemRepository.updateQuantity(cartItem.getQuantity(), cartItem.getId());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "updated quantity", cartItemRepository.findById(cartItem.getId())));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }
}
