package com.moyo.oms.service;

import com.moyo.oms.dto.LoginRequest;
import com.moyo.oms.dto.LoginResponse;
import com.moyo.oms.dto.RegisterRequest;
import com.moyo.oms.dto.RegisterResponse;
import com.moyo.oms.exception.UsernameAlreadyExistsException;
import com.moyo.oms.model.Vendor;
import com.moyo.oms.repository.VendorRepository;
import com.moyo.oms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Vendor vendor = vendorRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), vendor.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(vendor.getId(), vendor.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return response;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (vendorRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        Vendor vendor = new Vendor();
        vendor.setUsername(request.getUsername());
        vendor.setPassword(passwordEncoder.encode(request.getPassword()));
        vendor.setName(request.getVendorName());

        Vendor savedVendor = vendorRepository.save(vendor);

        RegisterResponse response = new RegisterResponse();
        response.setVendorId(savedVendor.getId());
        response.setUsername(savedVendor.getUsername());
        response.setVendorName(savedVendor.getName());
        response.setMessage("Registration successful");
        return response;
    }
}
