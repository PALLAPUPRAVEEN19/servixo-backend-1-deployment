package com.servixo.service;

import com.servixo.entity.ServiceEntity;
import com.servixo.entity.User;
import com.servixo.repository.ServiceRepository;
import com.servixo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    // 🔹 CREATE SERVICE (Professional)
    public ServiceEntity createService(Long professionalId, ServiceEntity request) {

        User user = userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + professionalId));

        // ✅ Only PROFESSIONAL can create services
        if (!user.getRole().getName().equalsIgnoreCase("PROFESSIONAL")) {
            throw new RuntimeException("Only professionals can create services");
        }

        // 🔥 CRITICAL FIX: Ensure title is NOT NULL
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            // Fallback to name if title is null (in case frontend still sends 'name')
            throw new RuntimeException("Service title cannot be null");
        }

        ServiceEntity service = new ServiceEntity();

        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());

        service.setProfessional(user);
        service.setStatus("PENDING");

        ServiceEntity savedService = serviceRepository.save(service);
        
        // Add log as requested
        System.out.println("SERVICE SAVED: " + savedService.getTitle());

        return savedService;
    }

    // 🔹 ADMIN - GET ALL SERVICES
    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    // 🔹 ADMIN - APPROVE SERVICE
    public ServiceEntity approveService(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        service.setStatus("APPROVED");
        return serviceRepository.save(service);
    }

    // 🔹 ADMIN - REJECT SERVICE
    public ServiceEntity rejectService(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        service.setStatus("REJECTED");
        return serviceRepository.save(service);
    }

    // 🔹 USER - GET ONLY APPROVED SERVICES
    public List<ServiceEntity> getApprovedServices() {
        return serviceRepository.findByStatus("APPROVED");
    }

    // 🔹 PROFESSIONAL - GET OWN SERVICES
    public List<ServiceEntity> getServicesByProfessional(Long professionalId) {

        if (!userRepository.existsById(professionalId)) {
            throw new RuntimeException("Professional not found with id: " + professionalId);
        }

        return serviceRepository.findByProfessional_Id(professionalId);
    }

    // 🔹 SEARCH SERVICES
    public List<ServiceEntity> searchServices(String keyword) {
        return serviceRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    // 🔹 DELETE SERVICE
    public void deleteService(Long id) {

        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Service not found with id: " + id);
        }

        serviceRepository.deleteById(id);
    }
}