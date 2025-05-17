package com.cac.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int billId;
    private LocalDate billDate;
    private double consultationFees;
    private double medicineFees;
    private double testCharges;
    private double miscellaneousCharge;
    private String description;
    private boolean isInsuranceApplicable;
    private float discountPercentage;
    private boolean tax;
    private double totalAmount;
    
    @JsonManagedReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id")
    private Appointment appObj;

    @OneToMany(mappedBy = "billObj", cascade = CascadeType.ALL)
    private List<Payment> paymentList;

    // Getters and Setters
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public double getConsultationFees() {
        return consultationFees;
    }

    public void setConsultationFees(double consultationFees) {
        this.consultationFees = consultationFees;
    }

    public double getMedicineFees() {
        return medicineFees;
    }

    public void setMedicineFees(double medicineFees) {
        this.medicineFees = medicineFees;
    }

    public double getTestCharges() {
        return testCharges;
    }

    public void setTestCharges(double testCharges) {
        this.testCharges = testCharges;
    }

    public double getMiscellaneousCharge() {
        return miscellaneousCharge;
    }

    public void setMiscellaneousCharge(double miscellaneousCharge) {
        this.miscellaneousCharge = miscellaneousCharge;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isInsuranceApplicable() {
        return isInsuranceApplicable;
    }

    public void setInsuranceApplicable(boolean insuranceApplicable) {
        isInsuranceApplicable = insuranceApplicable;
    }

    public float getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(float discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isTax() {
        return tax;
    }

    public void setTax(boolean tax) {
        this.tax = tax;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Appointment getAppObj() {
        return appObj;
    }

    public void setAppObj(Appointment appObj) {
        this.appObj = appObj;
    }

    public List<Payment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }
}