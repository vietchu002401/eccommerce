package com.vti.ecommerce.service;

import com.vti.ecommerce.config.JwtService;
import com.vti.ecommerce.dto.UserDTO;
import com.vti.ecommerce.exception.NotFoundException;
import com.vti.ecommerce.exception.ServerErrorException;
import com.vti.ecommerce.response.ResponseData;
import com.vti.ecommerce.user.User;
import com.vti.ecommerce.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomerService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<ResponseData> getUserInfo(HttpServletRequest request) throws ServerErrorException {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        UserDTO userDTO = UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .status(user.isStatus())
            .phone(user.getPhone())
            .email(user.getEmail())
            .createdDate(user.getCreatedDate())
            .updatedDate(user.getUpdatedDate())
            .build();
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Request successfully", userDTO));
    }

    public ResponseEntity<ResponseData> editProfile(UserDTO userDTO) throws ServerErrorException {
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new NotFoundException("User not found"));
        user.setAddress(user.getAddress());
        user.setPhone(user.getPhone());
        user.setEmail(user.getEmail());
        user.setUpdatedDate(new Date());
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Updated", userRepository.save(user)));
    }
}
