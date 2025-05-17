package com.cac.dto;

public class PaymentMethodAnalysisDTO {

    private long creditCardCount;
    private long debitCardCount;
    private long upiCount;
    private long cashCount;

    public PaymentMethodAnalysisDTO(long creditCardCount, long debitCardCount, long upiCount, long cashCount) {
        this.creditCardCount = creditCardCount;
        this.debitCardCount = debitCardCount;
        this.upiCount = upiCount;
        this.cashCount = cashCount;
    }

    // Getters and Setters
    public long getCreditCardCount() {
        return creditCardCount;
    }

    public void setCreditCardCount(long creditCardCount) {
        this.creditCardCount = creditCardCount;
    }

    public long getDebitCardCount() {
        return debitCardCount;
    }

    public void setDebitCardCount(long debitCardCount) {
        this.debitCardCount = debitCardCount;
    }

    public long getUpiCount() {
        return upiCount;
    }

    public void setUpiCount(long upiCount) {
        this.upiCount = upiCount;
    }

    public long getCashCount() {
        return cashCount;
    }

    public void setCashCount(long cashCount) {
        this.cashCount = cashCount;
    }
}
