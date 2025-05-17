package com.cac.dto;

import java.time.LocalDate;

public class PaymentSummaryDTO {
    private LocalDate date;
    private double cashTotal;
    private double creditCardTotal;
    private double debitCardTotal;
    private double upiTotal;
    private double totalAmount;

    public PaymentSummaryDTO(LocalDate date, double cashTotal, double creditCardTotal, double debitCardTotal, double upiTotal, double totalAmount) {
        this.date = date;
        this.cashTotal = cashTotal;
        this.creditCardTotal = creditCardTotal;
        this.debitCardTotal = debitCardTotal;
        this.upiTotal = upiTotal;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getCashTotal() { return cashTotal; }
    public void setCashTotal(double cashTotal) { this.cashTotal = cashTotal; }

    public double getCreditCardTotal() { return creditCardTotal; }
    public void setCreditCardTotal(double creditCardTotal) { this.creditCardTotal = creditCardTotal; }

    public double getDebitCardTotal() { return debitCardTotal; }
    public void setDebitCardTotal(double debitCardTotal) { this.debitCardTotal = debitCardTotal; }

    public double getUpiTotal() { return upiTotal; }
    public void setUpiTotal(double upiTotal) { this.upiTotal = upiTotal; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
