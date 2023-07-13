package com.vti.ecommerce.service;

import com.vti.ecommerce.dto.CartItemDTO;
import com.vti.ecommerce.model.Cart;
import com.vti.ecommerce.model.CartItem;
import com.vti.ecommerce.model.Product;
import com.vti.ecommerce.repository.CartItemRepository;
import com.vti.ecommerce.repository.CartRepository;
import com.vti.ecommerce.repository.ProductRepository;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private List<CartItemDTO> convertToCartItemDTO(List<CartItem> cartItemList) {
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            Optional<Product> productOptional = productRepository.findById(cartItem.getProductId());
            CartItemDTO cartItemDTO = CartItemDTO.builder()
                .id(cartItem.getId())
                .product(productOptional.isPresent() ? productOptional.get() : null)
                .cartId(cartItem.getCartId())
                .quantity(cartItem.getQuantity())
                .createdDate(cartItem.getCreatedDate())
                .updatedDate(cartItem.getUpdatedDate())
                .build();
            cartItemDTOS.add(cartItemDTO);
        }
        return cartItemDTOS;
    }

    public ResponseEntity<ResponseData> createCart(Cart cart) {
        try {
            if (cartRepository.existsByUserId(cart.getUserId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseData(HttpStatus.CONFLICT, "This user is already have a cart", null));
            }
            if(!userRepository.existsById(cart.getUserId())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseData(HttpStatus.BAD_REQUEST, "This user not found", null));
            }
            cart.setCreatedDate(new Date());
            cart.setUpdatedDate(new Date());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Created cart", cartRepository.save(cart)));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> addToCart(CartItem cartItem) {
        try {
            Optional<CartItem> cartItemOptional = cartItemRepository.findByProductIdAndCartId(cartItem.getProductId(), cartItem.getCartId());
            if (cartItemOptional.isPresent()) {
                CartItem c = cartItemOptional.get();
                c.setQuantity(c.getQuantity() + cartItem.getQuantity());
                c.setUpdatedDate(new Date());
                return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Add successfully", cartItemRepository.save(c)));
            }
            cartItem.setCreatedDate(new Date());
            cartItem.setUpdatedDate(new Date());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Add successfully", cartItemRepository.save(cartItem)));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> deleteCartItem(Long productId) {
        try {
            Optional<CartItem> cartItemOptional = cartItemRepository.findByProductId(productId);
            if (cartItemOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Item not found", null));
            }
            cartItemRepository.deleteById(cartItemOptional.get().getId());
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Deleted", null));
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

    public ResponseEntity<ResponseData> getCartItem(Long cartId) {
        try {
            List<CartItem> cartItemList = cartItemRepository.findALlByCartId(cartId);
            List<CartItemDTO> cartItemDTOS = convertToCartItemDTO(cartItemList);
            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", cartItemDTOS));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }

    public ResponseEntity<ResponseData> addQuantityToCart(String cartItemId, String quantity) {
        try {
           Optional<CartItem> cartItemOptional = cartItemRepository.findById(Long.valueOf(cartItemId));
           if(cartItemOptional.isEmpty()){
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseData(HttpStatus.NOT_FOUND, "Cart item not found", null));
           }
           CartItem cartItem = cartItemOptional.get();
           cartItem.setQuantity(cartItem.getQuantity() + Integer.valueOf(quantity));
           cartItem.setUpdatedDate(new Date());
           return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Added quantity", cartItemRepository.save(cartItem)));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null));
        }
    }
}
