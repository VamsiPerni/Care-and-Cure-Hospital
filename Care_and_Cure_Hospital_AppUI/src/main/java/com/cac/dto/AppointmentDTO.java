package com.cac.dto;

import com.cac.dto.PatientDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private int appointmentId;
    private PatientDTO patientObj;
	public int getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}
	public PatientDTO getPatientObj() {
		return patientObj;
	}
	public void setPatientObj(PatientDTO patientObj) {
		this.patientObj = patientObj;
	}
}