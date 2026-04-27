package com.servixo.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User {

    // ================= PRIMARY KEY =================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= BASIC FIELDS =================
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    // ================= ROLE =================
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // ================= EMAIL VERIFICATION =================
    @Column(nullable = false)
    private boolean isVerified = false;

    private String otp;

    private LocalDateTime otpExpiry;

    // ================= RELATIONSHIPS =================

    @JsonIgnore
    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL)
    private List<ServiceEntity> services;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Ticket> tickets;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Message> sentMessages;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Message> receivedMessages;

    // ================= CONSTRUCTORS =================
    public User() {}

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email.toLowerCase().trim(); // 🔥 FIX
        this.password = password;
        this.role = role;
        this.isVerified = false;
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.toLowerCase().trim(); // 🔥 FIX
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }

    public boolean isVerified() { return isVerified; }

    public void setVerified(boolean verified) { isVerified = verified; }

    public String getOtp() { return otp; }

    public void setOtp(String otp) { this.otp = otp; }

    public LocalDateTime getOtpExpiry() { return otpExpiry; }

    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }

    public List<ServiceEntity> getServices() { return services; }

    public void setServices(List<ServiceEntity> services) { this.services = services; }

    public List<Booking> getBookings() { return bookings; }

    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public List<Ticket> getTickets() { return tickets; }

    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }

    public List<Feedback> getFeedbacks() { return feedbacks; }

    public void setFeedbacks(List<Feedback> feedbacks) { this.feedbacks = feedbacks; }

    public List<Message> getSentMessages() { return sentMessages; }

    public void setSentMessages(List<Message> sentMessages) { this.sentMessages = sentMessages; }

    public List<Message> getReceivedMessages() { return receivedMessages; }

    public void setReceivedMessages(List<Message> receivedMessages) { this.receivedMessages = receivedMessages; }
}