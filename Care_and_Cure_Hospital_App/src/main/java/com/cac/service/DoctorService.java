package com.cac.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		Doctor savedDoctor= doctorRepository.save(doctor);
		if(!savedDoctor.getStatus()) {
			savedDoctor.setAvailableSlots(null);
		}
		//emailService.sendDoctorRegistrationEmail(savedDoctor.getEmailId(), savedDoctor.getDoctorName().getFirstName().toString());
		return savedDoctor; 
	}

	public Doctor updateDoctor(Doctor doctor) {
		DoctorValidation.validateDoctor(doctor);
		if(!doctor.getStatus()) {
			doctor.setAvailableSlots(null);
		}
		//emailService.sendDoctorUpdationEmail(doctor.getEmailId(), doctor.getDoctorName().getFirstName().toString());
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
    
//    public Doctor authenticate(Integer doctorId, String password) {
//        return doctorRepository.findByDoctorIdAndPassword(doctorId, password);
//    }
    
    public Long getTotalPatients(Integer doctorId) {
        return doctorRepository.countTotalPatients(doctorId);
    }

    public Long getTotalAppointments(Integer doctorId) {
        return doctorRepository.countTotalAppointments(doctorId);
    }

    public Double getTotalRevenue(Integer doctorId) {
        return doctorRepository.calculateTotalRevenue(doctorId);
    }

    public List<Appointment> getTodayAppointments(Integer doctorId) {
        return doctorRepository.findTodayAppointments(doctorId);
    }

    public List<Appointment> getUpcomingAppointments(Integer doctorId) {
        return doctorRepository.findUpcomingAppointments(doctorId);
    }

    public Map<String, Long> getPatientGenderDistribution(Integer doctorId) {
        List<Object[]> results = doctorRepository.getPatientGenderDistribution(doctorId);
        Map<String, Long> distribution = new HashMap<>();
        results.forEach(row -> distribution.put((String) row[0], (Long) row[1]));
        return distribution;
    }

    public Map<String, Long> getMonthlyAppointments(Integer doctorId) {
        List<Object[]> results = doctorRepository.getMonthlyAppointments(doctorId);
        Map<String, Long> monthly = new HashMap<>();
        results.forEach(row -> monthly.put((String) row[0], (Long) row[1]));
        return monthly;
    }

    public Map<String, Long> getMonthlyRevenue(Integer doctorId) {
        List<Object[]> results = doctorRepository.getMonthlyRevenue(doctorId);
        Map<String, Long> monthly = new HashMap<>();
        
        for (Object[] row : results) {
            String month = (String) row[0];
            Double revenueDouble = (Double) row[1]; // safely cast to Double
            Long revenueLong = revenueDouble.longValue(); // convert to Long
            monthly.put(month, revenueLong);
        }
        
        return monthly;
    }
}

	

