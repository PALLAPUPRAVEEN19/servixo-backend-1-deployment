package com.servixo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.servixo.entity.Booking;
import com.servixo.entity.ServiceEntity;
import com.servixo.entity.User;
import com.servixo.service.BookingService;
import com.servixo.service.ProfessionalService;
import com.servixo.service.ServiceService;

@RestController
@RequestMapping("/api/professional")
@CrossOrigin("*")
public class ProfessionalController {

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private BookingService bookingService;

    // ================= PROFILE =================
    @GetMapping("/{id}")
    public User getProfessional(@PathVariable Long id) {
        return professionalService.getProfessionalById(id);
    }

    // ================= ADD SERVICE =================
    @PostMapping("/services/{professionalId}")
    public ServiceEntity addService(
            @PathVariable Long professionalId,
            @RequestBody ServiceEntity service) {

        return serviceService.createService(professionalId, service);
    }

    // ================= VIEW OWN SERVICES =================
    @GetMapping("/services/{professionalId}")
    public List<ServiceEntity> getMyServices(@PathVariable Long professionalId) {
        return professionalService.getServicesByProfessional(professionalId);
    }

    // ================= VIEW BOOKINGS (MAIN FIX) =================
    @GetMapping("/bookings/{professionalId}")
    public List<Booking> getBookings(@PathVariable Long professionalId) {
        return bookingService.getProfessionalBookings(professionalId);
    }

    // ================= VIEW EARNINGS =================
  
  
}