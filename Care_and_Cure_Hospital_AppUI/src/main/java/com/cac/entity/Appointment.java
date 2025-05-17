package com.cac.entity;



import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    
    private int appointmentId;
    
    private LocalDate appointmentDate;
    private String slot;
    
    private String reasonForVisit;
    private String doctorReport;
    private String medicinesSuggested;

    private String status = "Scheduled";
    private Patient patient;
    private Doctor doctor;

    private Integer patientId;
    private Integer doctorId;

    // Constructors
    public Appointment() {}

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    

    public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public String getDoctorReport() {
        return doctorReport;
    }

    public void setDoctorReport(String doctorReport) {
        this.doctorReport = doctorReport;
    }

    public String getMedicinesSuggested() {
        return medicinesSuggested;
    }

    public void setMedicinesSuggested(String medicinesSuggested) {
        this.medicinesSuggested = medicinesSuggested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getters and Setters for Patient
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Integer getPatientId() {
        return (patient != null) ? patient.getPatientId() : patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    // Getters and Setters for Doctor
    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Integer getDoctorId() {
        return (doctor != null) ? doctor.getDoctorId() : doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }
}