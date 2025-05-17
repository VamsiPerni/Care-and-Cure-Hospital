package com.cac.service;


import com.cac.entity.Doctor;
import com.cac.entity.Patient;
import com.cac.exception.InvalidEntityException;
import com.cac.exception.PatientNotFoundException;
import com.cac.repository.PatientRepository;
import com.cac.validation.DoctorValidation;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientService {
  
	@Autowired
    private PatientRepository patientRepository;
	@Autowired
    private JavaMailSender mailSender;

//    public PatientService(PatientRepository patientRepository, JavaMailSender mailSender) {
//        this.patientRepository = patientRepository;
//        this.mailSender = mailSender;
//    }

    // Add new patient
    public Patient addPatient(Patient patient) throws InvalidEntityException {
        validatePatientEntity(patient);

        patient.setStatus(true);
        patient.setLastVisitDate(LocalDate.now());

        Patient savedPatient = patientRepository.save(patient);

        // Send welcome email
//        sendEmail(savedPatient.getEmailId(),
//                  "Welcome to Care and Cure Hospital",
//                  "Dear " + savedPatient.getPatientName() + ", your registration is successful.");

        return savedPatient;
    }

    // Get patient by ID
    public Patient getPatientById(int id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
    }
    


    // Update patient details
    public Patient updatePatient(int id, Patient updated) throws InvalidEntityException {
        Patient existing = getPatientById(id);

        validatePatientEntity(updated);

        existing.setPatientName(updated.getPatientName());
        existing.setContactNumber(updated.getContactNumber());
        existing.setEmailId(updated.getEmailId());
        existing.setLocation(updated.getLocation());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setGender(updated.getGender());
        existing.setAllergies(updated.getAllergies());
        existing.setMedications(updated.getMedications());
        existing.setTreatments(updated.getTreatments());
        existing.setMedicalHistory(updated.getMedicalHistory());
        existing.setOthers(updated.getOthers());
        existing.setDoctorId(updated.getDoctorId());
        existing.setStatus(updated.isStatus());
        // Mark as recently visited
        existing.setLastVisitDate(LocalDate.now());

        Patient saved = patientRepository.save(existing);

        // Send update notification
//        sendEmail(saved.getEmailId(),
//                  "Profile Updated",
//                  "Dear " + saved.getPatientName() + ", your profile has been updated.");

        return saved;
    }

    // Deactivate a patient
    public void deactivatePatient(int id) {
        Patient patient = getPatientById(id);
        patient.setStatus(false);
        patientRepository.save(patient);

        // Send deactivation email
        sendEmail(patient.getEmailId(),
                  "Profile Deactivated",
                  "Dear " + patient.getPatientName() + ", your profile has been deactivated.");
    }

    // Filter by status (active/inactive)
    public List<Patient> getPatientsByStatus(String status) {
        boolean isActive = status.equalsIgnoreCase("active");
        return patientRepository.findByStatus(isActive);
    }

    // Filter by doctor
    public List<Patient> getPatientsByDoctor(int doctorId) {
        return patientRepository.findByDoctorId(doctorId);
    }

    // Get only the medical history
    public String getMedicalHistory(int id) {
        return getPatientById(id).getMedicalHistory();
    }

    // Report #1: Daily Appointment Report (dummy logic using lastVisitDate)
    public String generateDailyAppointmentReport(LocalDate date) {
        List<Patient> allPatients = patientRepository.findAll();
        long count = allPatients.stream()
                .filter(p -> date.equals(p.getLastVisitDate()))
                .count();

        return "Daily Appointment Report for " + date + ": " + count + " appointments.";
    }

    // Report #2: Patient No-Show Report (dummy logic)
    public String generateNoShowReport(LocalDate startDate, LocalDate endDate) {
        return "Patient No-Show Report from " + startDate + " to " + endDate
                + ": [No real data available in this demo].";
    }

    // Report #3: Patients by Health Issue
    public String generatePatientsByHealthIssueReport(String issue) {
        List<Patient> matches = patientRepository.findByMedicalHistoryContaining(issue);
        if (matches.isEmpty()) {
            return "No patients found with health issue containing: " + issue;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Patients with health issue '").append(issue).append("':\n");
        for (Patient p : matches) {
            sb.append("ID: ").append(p.getPatientId())
              .append(", Name: ").append(p.getPatientName())
              .append("\n");
        }
        return sb.toString();
    }

    // Deactivate patients who haven't visited in 2+ years
    public void deactivateInactivePatients() {
        LocalDate cutoffDate = LocalDate.now().minusYears(2);
        List<Patient> inactive = patientRepository.findInactivePatients(cutoffDate);

        for (Patient p : inactive) {
            p.setStatus(false);
            patientRepository.save(p);

            // Send inactivity email
            sendEmail(p.getEmailId(),
                      "Profile Deactivated due to Inactivity",
                      "Dear " + p.getPatientName()
                      + ", your profile has been deactivated due to 2+ years of inactivity.");
        }
    }

    // Basic validation that can throw InvalidEntityException
    private void validatePatientEntity(Patient patient) throws InvalidEntityException {
        if (patient.getPatientName() == null || patient.getPatientName().length() < 3) {
            throw new InvalidEntityException("Patient name must be at least 3 characters long.");
        }
        if (patient.getContactNumber() == null || !patient.getContactNumber().matches("\\d{10}")) {
            throw new InvalidEntityException("Contact number must be exactly 10 digits.");
        }
        if (patient.getEmailId() == null || !patient.getEmailId().contains("@")) {
            throw new InvalidEntityException("Invalid email address.");
        }
        // Additional checks as needed...
    }

    // Helper: Send email safely
    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
        }
    }
}
