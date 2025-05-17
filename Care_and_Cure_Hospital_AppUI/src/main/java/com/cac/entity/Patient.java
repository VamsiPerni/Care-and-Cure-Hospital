package com.cac.entity;

import java.time.LocalDate;

public class Patient {

    private int patientId;
	private String patientName;
	private String contactNumber;
    private String emailId;
    private String location;
    private LocalDate dateOfBirth;
    private String gender;
    private String allergies;
    private String medications;
    private String treatments;
    private String medicalHistory;
    private String others;

    // Tracks the last visit date for inactivity checks
    private LocalDate lastVisitDate;

    // true = active, false = inactive
    private boolean status;

    // Simple association with a doctor (by ID).
    // In a real app, you'd have a Doctor entity with a @ManyToOne or similar.
    private int doctorId;

    public Patient() {
        this.status = true; // default active
        this.lastVisitDate = LocalDate.now();
    }

    // For convenience, you can add more constructors if needed.

    public int getPatientId() {
        return patientId;
    }
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getContactNumber() {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmailId() {
        return emailId;
    }
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAllergies() {
        return allergies;
    }
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedications() {
        return medications;
    }
    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getTreatments() {
        return treatments;
    }
    public void setTreatments(String treatments) {
        this.treatments = treatments;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getOthers() {
        return others;
    }
    public void setOthers(String others) {
        this.others = others;
    }

    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }
    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getDoctorId() {
        return doctorId;
    }
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

}
