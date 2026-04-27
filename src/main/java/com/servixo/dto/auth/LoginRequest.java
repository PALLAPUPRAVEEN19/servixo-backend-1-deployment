package com.servixo.dto.auth;

public class LoginRequest {

    private String email;
    private String password;

    public LoginRequest() {}

    public String getEmail() {
        return email == null ? null : email.trim().toLowerCase().replace(" ", "");
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // 🔥 FIX: sanitize password
    public String getPassword() {
        return password == null ? null : password.trim();
    }

    public void setPassword(String password) {
        this.password = password;
    }
}