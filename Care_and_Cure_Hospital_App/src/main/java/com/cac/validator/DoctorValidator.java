package com.cac.validator;

import com.cac.entity.Doctor;
import com.cac.entity.Name;
import com.cac.exception.InvalidEntityException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class DoctorValidator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern CONTACT_NUMBER_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern LOCATION_PATTERN = Pattern.compile("^[a-zA-Z\\s,]+$");

    public static void validateDoctor(Doctor doctor) {
        validateName(doctor.getDoctorName());
        validateSpecialization(doctor.getSpecialization());
        validateQualification(doctor.getQualification());
        validateContactNumber(doctor.getContactNumber());
        validateEmail(doctor.getEmailId());
        validateGender(doctor.getGender());
        validateLocation(doctor.getLocation());
        validateConsultationFee(doctor.getConsultationFees());
        validateDateOfJoining(doctor.getDateOfJoining());
        validateYearsOfExperience(doctor.getYearsOfExperience());
        validateAvailableSlots(doctor.getAvailableSlots());
    }

    private static void validateName(Name name) {
        if (name == null || name.getFirstName() == null || name.getLastName() == null) {
            throw new InvalidEntityException("Doctor name cannot be null.");
        }
        if (!NAME_PATTERN.matcher(name.getFirstName()).matches() || !NAME_PATTERN.matcher(name.getLastName()).matches()) {
            throw new InvalidEntityException("Doctor name should only contain alphabets.");
        }
    }

    private static void validateSpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new InvalidEntityException("Specialization cannot be null or empty.");
        }
        if (!NAME_PATTERN.matcher(specialization).matches()) {
            throw new InvalidEntityException("Specialization should only contain alphabets.");
        }
    }

    private static void validateQualification(String qualification) {
        if (qualification == null || qualification.trim().isEmpty()) {
            throw new InvalidEntityException("Qualification cannot be null or empty.");
        }
    }

    private static void validateContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new InvalidEntityException("Contact number cannot be null or empty.");
        }
        if (!CONTACT_NUMBER_PATTERN.matcher(contactNumber).matches()) {
            throw new InvalidEntityException("Contact number should be 10 digits.");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEntityException("Email cannot be null or empty.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEntityException("Invalid email format.");
        }
    }
    
    private static void validateGender(Doctor.Gender gender) {
        if (gender == null) {
            throw new InvalidEntityException("Gender cannot be null.");
        }
        if (!(gender.equals(Doctor.Gender.MALE) || gender.equals(Doctor.Gender.FEMALE) || gender.equals(Doctor.Gender.OTHER))) {
            throw new InvalidEntityException("Gender must be one of: MALE, FEMALE, or OTHER.");
        }
    }

    private static void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new InvalidEntityException("Location cannot be null or empty.");
        }
        if (!LOCATION_PATTERN.matcher(location).matches()) {
            throw new InvalidEntityException("Location should only contain alphabets and commas.");
        }
    }
    
    private static void validateConsultationFee(double consultationFees) {
        if (consultationFees < 0) {
            throw new InvalidEntityException("Consultation Fee cannot be negative.");
        }
    }
    
    
    private static void validateDateOfJoining(LocalDate dateOfJoining) {
        if (dateOfJoining.isAfter(LocalDate.now())) {
            throw new InvalidEntityException("Date of joining cannot be in the future.");
        }
    }
    
    private static void validateYearsOfExperience(int yearsOfExperience) {
        if (yearsOfExperience < 0) {
            throw new InvalidEntityException("Years of experience cannot be negative.");
        }
    }
    
    private static void validateAvailableSlots(List<LocalDateTime> availableSlots) {
    	for (LocalDateTime slot : availableSlots) {
            if (slot == null) { // Prevent NullPointerException
                throw new InvalidEntityException("Available slot cannot be null.");
            }
            if (slot.isBefore(LocalDateTime.now())) {
                throw new InvalidEntityException("Available slots cannot be in the past.");
            }
        }
        if (availableSlots == null || availableSlots.isEmpty()) {
        	throw new InvalidEntityException("Available slots cannot be null.");
        }
    }
}