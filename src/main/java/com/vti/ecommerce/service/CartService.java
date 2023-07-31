package com.vti.ecommerce.service;

import com.vti.ecommerce.config.JwtService;
import com.vti.ecommerce.exception.BadRequestException;
import com.vti.ecommerce.exception.ConflictException;
import com.vti.ecommerce.exception.NotFoundException;
import com.vti.ecommerce.exception.ServerErrorException;
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

    public ResponseEntity<ResponseData> createCart(HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
            if (cartRepository.existsByUserId(user.getId())) {
                throw new ConflictException("This user is already have a cart");
            }
            Cart cart = Cart.builder()
                .userId(user.getId())
                .status(true)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created cart", cartRepository.save(cart)));
        } catch (NotFoundException | ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public ResponseEntity<ResponseData> addToCart(Long productId, HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found"));
            if (!cartRepository.existsByUserId(user.getId())) {
                Cart cart = Cart.builder()
                    .userId(user.getId())
                    .status(true)
                    .createdDate(new Date())
                    .updatedDate(new Date())
                    .build();
                cartRepository.save(cart);
            }
            Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(()-> new NotFoundException("Cart not found"));
            Long cartId = cart.getId();
            Product product = productRepository.findById(productId).orElseThrow(()-> new NotFoundException("Product not found"));
            Optional<CartItem> cartItemOptional = cartItemRepository.findByProductIdAndCartId(productId, cartId);
            if (cartItemOptional.isPresent()) {
                CartItem c = cartItemOptional.get();
                c.setQuantity(c.getQuantity() + 1);
                c.setSubTotal(c.getSubTotal() + product.getPrice());
                c.setUpdatedDate(new Date());
                if (product.getAmount() < c.getQuantity()) {
                    throw new BadRequestException("The product has only " + product.getAmount() + " items left");
                }
                return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Add successfully", cartItemRepository.save(c)));
            }
            if (product.getAmount() < 1) {
                throw new BadRequestException("The product has only " + product.getAmount() + " items left");
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
        } catch (BadRequestException | NotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
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
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "User not found", null));
            }
            User user = userOptional.get();
            Optional<Cart> cartOptional = cartRepository.findByUserId(user.getId());
            if (cartOptional.isEmpty()) {
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
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "User not found", null));
            }
            User user = userOptional.get();
            Optional<Cart> cartOptional = cartRepository.findByUserId(user.getId());
            if (cartOptional.isEmpty()) {
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
            if (productOptional.isPresent()) {
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
