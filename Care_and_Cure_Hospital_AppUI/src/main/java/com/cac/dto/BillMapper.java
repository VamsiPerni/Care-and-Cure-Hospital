package com.cac.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.cac.entity.Appointment;
import com.cac.entity.Bill;
import com.cac.entity.Patient;
import com.cac.entity.Payment;

@Component
public class BillMapper {
    
    public BillDTO toDto(Bill bill) {
        if (bill == null) {
            return null;
        }
        
        BillDTO dto = new BillDTO();
        // Map all Bill fields
        dto.setBillId(bill.getBillId());
        dto.setBillDate(bill.getBillDate());
        dto.setConsultationFees(bill.getConsultationFees());
        dto.setMedicineFees(bill.getMedicineFees());
        dto.setTestCharges(bill.getTestCharges());
        dto.setMiscellaneousCharge(bill.getMiscellaneousCharge());
        dto.setDescription(bill.getDescription());
        dto.setInsuranceApplicable(bill.isInsuranceApplicable());
        dto.setDiscountPercentage(bill.getDiscountPercentage());
        dto.setTax(bill.isTax());
        dto.setTotalAmount(bill.getTotalAmount());
        
        // Map payment status from PaymentEntity
        if (bill.getPaymentList() != null && !bill.getPaymentList().isEmpty()) {
            // Assuming you want the status from the most recent payment
            Payment latestPayment = bill.getPaymentList().get(0);
            dto.setPaymentStatus(latestPayment.getPaymentStatus());
        } else {
            dto.setPaymentStatus("UNPAID");
        }
        
        // Map appointment data
        if (bill.getAppObj() != null) {
            dto.setAppObj(toAppointmentDto(bill.getAppObj()));
        }
        
        return dto;
    }
    
    public AppointmentDTO toAppointmentDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentId(appointment.getAppointmentId());
        
        if (appointment.getPatient() != null) {
            dto.setPatientObj(toPatientDto(appointment.getPatient()));
        }
        
        return dto;
    }
    
    public PatientDTO toPatientDto(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        PatientDTO dto = new PatientDTO();
        dto.setPatientId(patient.getPatientId());
        dto.setPatientName(patient.getPatientName());
        return dto;
    }
    
    public List<BillDTO> toDtoList(List<Bill> bills) {
        return bills.stream()
                   .map(this::toDto)
                   .collect(Collectors.toList());
    }
}