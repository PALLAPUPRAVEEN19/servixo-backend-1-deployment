package com.servixo.service;

import com.servixo.entity.User;
import com.servixo.entity.Role;
import com.servixo.repository.UserRepository;
import com.servixo.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${google.client.id}")
    private String googleClientId;

    // ================= REGISTER =================
    public User register(String name, String email, String password, String roleName) {

        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // ✅ Normalize role (CRITICAL FIX)
        String finalRole = (roleName == null || roleName.trim().isEmpty())
                ? "USER"
                : roleName.trim().toUpperCase();

        System.out.println("ROLE RECEIVED: " + roleName);
        System.out.println("ROLE USED: " + finalRole);

        Role role = roleRepository.findByNameIgnoreCase(finalRole) // ✅ safer
                .orElseThrow(() -> new RuntimeException("Role not found: " + finalRole));

        User user = new User();
        user.setName(name);
        user.setEmail(email);

        // ✅ SECURE PASSWORD
        user.setPassword(passwordEncoder.encode(password));

        user.setRole(role);

        return userRepository.save(user);
    }

    // ================= LOGIN + SEND OTP =================
    public Map<String, Object> loginAndSendOtp(String email, String password) {

        User user = userRepository.findByEmailWithRole(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ PASSWORD CHECK (FIXED)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 🔥 Generate OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        // 🔥 Send Email
        emailService.sendEmail(
                email,
                "Servixo Login OTP",
                "Your OTP is: " + otp + "\nValid for 5 minutes."
        );

        System.out.println("🔥 LOGIN OTP: " + otp);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "OTP_REQUIRED");
        response.put("email", email);

        return response;
    }

    // ================= VERIFY LOGIN OTP =================
    public User verifyLoginOtp(String email, String otp) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        // ✅ Clear OTP
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return user;
    }

    // ================= RESEND OTP =================
    public void sendOtp(String email) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        emailService.sendEmail(
                email,
                "Servixo OTP",
                "Your OTP is: " + otp
        );
    }

    // ================= GOOGLE LOGIN =================
    public User googleLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                return userRepository.findByEmailIgnoreCase(email)
                        .orElseGet(() -> {
                            // Create new user if not found
                            User newUser = new User();
                            newUser.setName(name);
                            newUser.setEmail(email);
                            // Set a dummy password for SSO users
                            newUser.setPassword(passwordEncoder.encode("GOOGLE_SSO_" + System.currentTimeMillis()));
                            newUser.setVerified(true);
                            
                            Role userRole = roleRepository.findByNameIgnoreCase("USER")
                                    .orElseThrow(() -> new RuntimeException("Default USER role not found"));
                            newUser.setRole(userRole);
                            
                            return userRepository.save(newUser);
                        });
            } else {
                throw new RuntimeException("Invalid Google ID token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verifying Google token: " + e.getMessage());
        }
    }
}