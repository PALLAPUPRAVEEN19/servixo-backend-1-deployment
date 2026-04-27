package com.servixo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.servixo.dto.EmailRequest;
import com.servixo.dto.LoginDto;
import com.servixo.dto.VerifyOtpRequest;
import com.servixo.dto.auth.RegisterRequest;
import com.servixo.entity.User;
import com.servixo.security.JwtUtil;
import com.servixo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        User user = authService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRoleName()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getName()); // ✅ added for clarity

        return ResponseEntity.ok(response);
    }

    // ================= LOGIN (STEP 1 - SEND OTP) =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {

        Map<String, Object> response = authService.loginAndSendOtp(
                dto.getEmail(),
                dto.getPassword()
        );

        return ResponseEntity.ok(response);
    }

    // ================= VERIFY LOGIN OTP =================
    @PostMapping("/verify-login-otp")
    public ResponseEntity<?> verifyLoginOtp(@RequestBody VerifyOtpRequest request) {

        User user = authService.verifyLoginOtp(
                request.getEmail(),
                request.getOtp()
        );

        // 🔥 IMPROVED TOKEN (optional but better)
        String role = user.getRole().getName();
        String token = jwtUtil.generateToken(
                user.getEmail(),
                role
        );
        
        System.out.println("ROLE SAVED: " + role);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("role", role); // ✅ IMPORTANT
        response.put("name", user.getName());

        return ResponseEntity.ok(response);
    }

    // ================= RESEND OTP =================
    @PostMapping("/send-otp")
    public ResponseEntity<?> resendOtp(@RequestBody EmailRequest request) {

        authService.sendOtp(request.getEmail());

        return ResponseEntity.ok("OTP sent successfully");
    }

    // ================= GOOGLE LOGIN =================
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        String idToken = request.get("token");
        User user = authService.googleLogin(idToken);

        String role = user.getRole().getName();
        String token = jwtUtil.generateToken(user.getEmail(), role);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("role", role);
        response.put("name", user.getName());

        return ResponseEntity.ok(response);
    }
}