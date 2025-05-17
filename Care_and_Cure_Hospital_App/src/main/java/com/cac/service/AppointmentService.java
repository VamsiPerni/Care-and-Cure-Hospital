package com.cac.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cac.entity.Appointment;
import com.cac.entity.Doctor;
import com.cac.entity.Patient;
import com.cac.repository.AppointmentRepository;
import com.cac.repository.DoctorRepository;
import com.cac.repository.PatientRepository;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private EmailService emailService;
    
    public List<Appointment> viewAppointmentsByDoctor(Integer doctorId, LocalDate date) {
        return appointmentRepository.findByDoctor_DoctorIdAndAppointmentDate(doctorId, date);
    }

 
    public List<Appointment> viewAppointmentsByPatient(Integer patientId) {
        return appointmentRepository.findByPatient_PatientId(patientId);
    }
    
    
//
    // Fetch upcoming appointments for a patient
    public List<Appointment> viewUpcomingAppointments(Integer patientId) {
        return appointmentRepository.findByPatient_PatientIdAndAppointmentDateAfter(patientId, LocalDate.now());
    }
//
//    public Appointment addAppointment(Appointment appointment) {
//        boolean exists = appointmentRepository.existsByAppointmentDateAndAppointmentStartTime(
//            appointment.getAppointmentDate(), 
//            appointment.getAppointmentStartTime()
//        );
//
//        if (exists) {
//            throw new IllegalStateException("Appointment already exists at the given date and time.");
//        }
//
//        return appointmentRepository.save(appointment);
//    }
//    
    public Appointment addAppointment(Appointment appointment) {
        System.out.println("Received Appointment Data: " + appointment);
if(appointment.getDoctorId() == null && appointment.getPatientId() == null) {
	throw new IllegalArgumentException(" Doctor ID is null and patientid null");
}
        else if(appointment.getDoctorId() == null) {
        	throw new IllegalArgumentException(" Doctor ID is null");
        }
        
        else if (appointment.getPatientId() == null ) {
            throw new IllegalArgumentException("Patient ID ");
        }
        
        

        Integer patientId = appointment.getPatientId();
        Integer doctorId = appointment.getDoctorId();

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));

        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        boolean exists = appointmentRepository.existsByAppointmentDateAndSlot(
            appointment.getAppointmentDate(), 
            appointment.getSlot()
        );

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment already exists.");
        }


        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        System.out.println("Saved Appointment: " + savedAppointment);

        return savedAppointment;
    }



//    public Appointment addAppointment(Appointment appointment) {
//        Integer patientId = appointment.getPatientId();
//        Integer doctorId = appointment.getDoctorId();
//        Patient patient = patientRepository.findById(patientId)
//            .orElseThrow(() -> new RuntimeException("Patient not found"));
//
//        Doctor doctor = doctorRepository.findById(doctorId)
//            .orElseThrow(() -> new RuntimeException("Doctor not found"));
//
//        boolean exists = appointmentRepository.existsByAppointmentDateAndAppointmentStartTime(
//            appointment.getAppointmentDate(), 
//            appointment.getAppointmentStartTime()
//        );
//
//        if (exists) {
//            throw new IllegalStateException("Appointment already exists at the given date and time.");
//        }
//
//        appointment.setPatient(patient);
//        appointment.setDoctor(doctor);
//        Appointment savedAppointment = appointmentRepository.save(appointment);
//
//        // Send email notification
//        String patientEmail = (patient.getEmailId() != null) ? patient.getEmailId() : "default@example.com";
//        emailService.sendAppointmentConfirmation(patientEmail, savedAppointment);  
//
//        return savedAppointment;
//    }


    public List<Appointment> viewAllAppointment() {
    	List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentDate)
                        .thenComparing(Appointment::getSlot)) // Sorts by Date then Slot (Time)
                .collect(Collectors.toList());
    }
 
    public Appointment viewAppointmentById(Integer id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public List<Appointment> viewAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date); 
    }

    public List<Appointment> viewCancelledAppointments(LocalDate date) {
        return appointmentRepository.findByAppointmentDateAndStatus(date, "Cancelled");
    }
    public List<Appointment> generateHealthIssueAppointmentReport(String healthIssue, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByReasonForVisitAndAppointmentDateBetween(healthIssue, startDate, endDate);
    }
    public List<Appointment> generateAppointmentCancellationReport(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByStatusAndAppointmentDateBetween("CANCELLED", startDate, endDate);
    }

    public void cancelAppointment(Integer id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if(appointment.isPresent()) {
        	Appointment cancelAppointment =appointment.get();
        	cancelAppointment.setStatus("Cancelled");
        	appointmentRepository.save(cancelAppointment);
        }else {
        	 throw new IllegalArgumentException("Appointment not found");
        }
//        appointmentRepository.deleteById(id);
    }
    public Appointment rescheduleAppointment(Integer id, Appointment updatedAppointment) {
        Appointment existingAppointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found for ID: " + id));

        
        boolean exists = appointmentRepository.existsByAppointmentDateAndSlot(
            updatedAppointment.getAppointmentDate(), 
            updatedAppointment.getSlot()
        );

        if (exists) {
            throw new IllegalStateException("Another appointment already exists at the given date and time.");
        }

        existingAppointment.setAppointmentDate(updatedAppointment.getAppointmentDate());
        existingAppointment.setSlot(updatedAppointment.getSlot());
        existingAppointment.setStatus("Rescheduled");
        Appointment savedAppointment = appointmentRepository.save(existingAppointment);

        return appointmentRepository.save(existingAppointment);
    }
    
    public List<Map<String, Object>> getAllAppointmentCounts() {
        List<LocalDate> allAppointmentDates = appointmentRepository.findDistinctAppointmentDates();
        List<Map<String, Object>> appointmentCountsList = new ArrayList<>();

        for (LocalDate date : allAppointmentDates) {
            Map<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("appointmentDate", date);
            appointmentData.put("scheduled", appointmentRepository.countByStatusAndAppointmentDate("Scheduled", date));
            appointmentData.put("rescheduled", appointmentRepository.countByStatusAndAppointmentDate("Rescheduled", date));
            appointmentData.put("cancelled", appointmentRepository.countByStatusAndAppointmentDate("Cancelled", date));
            appointmentCountsList.add(appointmentData);
        }
        return appointmentCountsList;
    }

   

    public List<Map<String, Object>> getTodaysAppointments(LocalDate today) {
        List<Appointment> appointments = appointmentRepository.findByAppointmentDate(today);

        return appointments.stream()
            .map(a -> {
                Map<String, Object> appointmentData = new HashMap<>();
                appointmentData.put("time", a.getSlot());
                appointmentData.put("status", a.getStatus());

                // Ensure doctor is initialized and extract full name
                Doctor doctor = a.getDoctor();
                if (doctor != null && doctor.getDoctorName() != null) {
                    appointmentData.put("doctorName", doctor.getDoctorName().getFirstName() + " " + doctor.getDoctorName().getLastName());
                } else {
                    appointmentData.put("doctorName", "Unknown Doctor");
                }

                // Ensure patient is initialized and extract patient name
                Patient patient = a.getPatient();
                if (patient != null) {
                    appointmentData.put("patientName", patient.getPatientName());
                } else {
                    appointmentData.put("patientName", "Unknown Patient");
                }

                return appointmentData;
            })
            .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> getAppointmentsByMonth() {
        List<Object[]> results = appointmentRepository.countAppointmentsByMonth();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            
            // Debugging output
            System.out.println("Processing Row: " + Arrays.toString(row));
            
            if (row[0] instanceof Number) {
                map.put("monthNumber", ((Number) row[0]).intValue()); // Month Number
            } else {
                System.out.println("Invalid monthNumber format: " + row[0]);
                continue; // Skip invalid rows
            }

            map.put("monthName", row[1].toString()); // Month Name

            if (row[2] instanceof Number) {
                map.put("appointmentCount", ((Number) row[2]).longValue()); // Appointment Count
            } else {
                System.out.println("Invalid appointmentCount format: " + row[2]);
                continue; // Skip invalid rows
            }

            response.add(map);
        }
        return response;
    }

    public Appointment getOngoingAppointment() {
        List<Appointment> todayAppointments = appointmentRepository.findByAppointmentDateAndStatus(
            LocalDate.now(), "Scheduled"
        );

        System.out.println("Scheduled appointments for today: " + todayAppointments);

        return todayAppointments.stream()
                .sorted(Comparator.comparing(Appointment::getSlot))
                .findFirst()
                .orElse(null);
    }



    public void updateAppointmentStatus(Integer appointmentId, String status) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findByAppointmentId(appointmentId);
        
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(status);
            appointmentRepository.save(appointment);

            System.out.println("Updated appointment " + appointmentId + " to status: " + status);
        } else {
            System.out.println("Appointment not found with ID: " + appointmentId);
        }
    }

    public Optional<Appointment> findById(Integer id) {
        return appointmentRepository.findByAppointmentId(id);
    }


    

    }


    

    
