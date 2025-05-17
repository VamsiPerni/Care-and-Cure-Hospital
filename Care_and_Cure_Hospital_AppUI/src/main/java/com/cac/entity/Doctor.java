package com.cac.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Doctor {
	
	private int doctorId;
	private Name doctorName;
	private String specialization;
	private String qualification;
	private String contactNumber;
	private String emailId;
	private Gender gender;
	private String location;
	private double consultationFees;
	private LocalDate dateOfJoining;
	private boolean isSurgeon;
	private int yearsOfExperience;
	private List<Appointment> appointmentList;
	private List<LocalDateTime> availableSlots;
	private List<LocalDate> consultationDate;
	private boolean status;
	
	public enum Gender {
        MALE, FEMALE, OTHER;
    }
	
	public int getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}
	public Name getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(Name doctorName) {
		this.doctorName = doctorName;
	}
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
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
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public double getConsultationFees() {
		return consultationFees;
	}
	public void setConsultationFees(double consultationFees) {
		this.consultationFees = consultationFees;
	}
	public LocalDate getDateOfJoining() {
		return dateOfJoining;
	}
	public void setDateOfJoining(LocalDate dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}
	public boolean getIsSurgeon() {
		return isSurgeon;
	}
	public void setIsSurgeon(boolean isSurgeon) {
		this.isSurgeon = isSurgeon;
	}
	public int getYearsOfExperience() {
		return yearsOfExperience;
	}
	public void setYearsOfExperience(int yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}
	public List<Appointment> getAppointmentList() {
		return appointmentList;
	}
	public void setAppointmentList(List<Appointment> appointmentList) {
		this.appointmentList = appointmentList;
	}
	public List<LocalDateTime> getAvailableSlots() {
		return availableSlots;
	}
	public void setAvailableSlots(List<LocalDateTime> availableSlots) {
		this.availableSlots = availableSlots;
	}
	public List<LocalDate> getConsultationDate() {
		return consultationDate;
	}
	public void setConsultationDate(List<LocalDate> consultationDate) {
		this.consultationDate = consultationDate;
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}	
	

}
