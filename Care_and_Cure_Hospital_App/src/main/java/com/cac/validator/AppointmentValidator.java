package com.cac.validator;

import com.cac.exception.InvalidEntityException;
import com.cac.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class AppointmentValidator {

    private static final Pattern PATIENT_NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern CONTACT_NUMBER_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    public static void validateAppointment(Appointment appointment) {
//        validatePatientName(appointment.getPatientId());
//        validateContactNumber(appointment.getPatientContactNumber());
//        validateEmail(appointment.getPatientEmail());
//        validateAppointmentDate(appointment.getAppointmentDate());
        validateDoctorId(appointment.getDoctorId());
    }
//
//    private static void validatePatientName(Integer integer) {
//        if (integer == null || integer.trim().isEmpty()) {
//            throw new InvalidEntityException("Patient name cannot be null or empty.");
//        }
//        if (!PATIENT_NAME_PATTERN.matcher(integer).matches()) {
//            throw new InvalidEntityException("Patient name should only contain alphabets.");
//        }
//    }

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

//    private static void validateAppointmentDate(LocalDate localDate) {
//        if (localDate == null) {
//            throw new InvalidEntityException("Appointment date cannot be null.");
//        }
//        if (localDate.isBefore(LocalDateTime.now())) {
//            throw new InvalidEntityException("Appointment date must be in the future.");
//        }
//    }

    private static void validateDoctorId(Integer integer) {
        if (integer == null || integer <= 0) {
            throw new InvalidEntityException("Invalid doctor ID.");
        }
    }
}
