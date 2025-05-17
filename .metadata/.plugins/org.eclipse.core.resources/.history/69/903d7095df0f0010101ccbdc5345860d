package com.cac.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.entity.Appointment;
import com.cac.entity.Doctor;
import com.cac.repository.DoctorRepository;
import com.cac.validation.DoctorValidation;


@Service
public class DoctorService {
     
	@Autowired
	DoctorRepository doctorRepository;
	
	@Autowired
	EmailService emailService;
	 
	public String layout() {
		return "layout";
	}
	
	public String home() {
		return "home";
	}
	
	public Doctor addDoctor(Doctor doctor) {
		DoctorValidation.validateDoctor(doctor);
		if (doctor.getAvailableSlots() == null || doctor.getAvailableSlots().isEmpty()) {
            doctor.setAvailableSlots(null); // Explicitly handle null case
        }
		Doctor savedDoctor= doctorRepository.save(doctor);
		// emailService.sendDoctorRegistrationEmail(savedDoctor.getEmailId(), savedDoctor.getDoctorName().getFirstName().toString());
		return savedDoctor; 
	}

	public Doctor updateDoctor(Doctor doctor) {
		DoctorValidation.validateDoctor(doctor);
		if (doctor.getAvailableSlots() == null || doctor.getAvailableSlots().isEmpty()) {
            doctor.setAvailableSlots(null); // Explicitly handle null case
        }
		return doctorRepository.save(doctor);
	}
	
	public Doctor viewDoctorById(int doctorId) {
		return doctorRepository.findById(doctorId).get();
	}
	
	public List<Doctor> viewAllDoctors() {
		return doctorRepository.findAll();
	}
	
	public List<Doctor> viewDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationIgnoreCase(specialization);
    }
	
	public List<Doctor> viewAllActiveDoctors() {
        return doctorRepository.findAllActiveDoctors();
    }
	
	public List<Doctor> viewDoctorAvailabilityBySlot(LocalDateTime slot) {
        return doctorRepository.findByAvailabilitySlot(slot);
    }

    public List<Doctor> viewDoctorAvailabilityByDay(LocalDate day) {
        return doctorRepository.findByAvailabilityDay(day);
    }
    
    public boolean deactivateDoctor(int doctorId) {
        int updatedRows = doctorRepository.deactivateDoctor(doctorId);
        return updatedRows > 0;
    }

    public List<Doctor> viewDoctorsByConsultationCount(LocalDate startDate, LocalDate endDate, int minCount) {
        return doctorRepository.findByConsultationCount(startDate, endDate, minCount);
    }
    
    public List<Appointment> generateDoctorAppointmentReport(int doctorId, LocalDate startDate, LocalDate endDate) {
        return doctorRepository.generateDoctorAppointmentReport(doctorId, startDate, endDate);
    }
    
    public Double generateDoctorRevenueReport(int doctorId, LocalDate startDate, LocalDate endDate) {
        Double revenue = doctorRepository.generateDoctorRevenueReport(doctorId, startDate, endDate);
         return revenue != null ? revenue : 0.0;  // Handle null values
    }

    public List<Doctor> generateLowConsultationDoctorsReport(LocalDate startDate, LocalDate endDate) {
         return doctorRepository.generateLowConsultationDoctorsReport(startDate, endDate);
    }
    

    public List<Doctor> generateMostFrequentlyBookedDoctorsReport(LocalDate startDate, LocalDate endDate) {
        return doctorRepository.generateMostFrequentlyBookedDoctorsReport(startDate, endDate);
    }

    public List<Doctor> generateDoctorAvailabilityReport(LocalDate date) {
        return doctorRepository.findDoctorAvailability(date);
    }
	
}
