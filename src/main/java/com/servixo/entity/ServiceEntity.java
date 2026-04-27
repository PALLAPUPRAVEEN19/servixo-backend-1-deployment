package com.servixo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private double price;

    private String status; // PENDING / APPROVED

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private User professional; // ✅ FIXED

    public ServiceEntity() {}

    // GETTERS & SETTERS

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }
    
    // Robust mapping alias to ensure JSON payloads with 'name' map successfully to 'title'
    public void setName(String name) { 
        if (this.title == null || this.title.isEmpty()) {
            this.title = name; 
        }
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public User getProfessional() { return professional; }

    public void setProfessional(User professional) { this.professional = professional; }
}