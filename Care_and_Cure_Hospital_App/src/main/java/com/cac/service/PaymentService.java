package com.cac.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.dto.PaymentSummaryDTO;
import com.cac.entity.Payment;
import com.cac.repository.PaymentRepository;
import jakarta.transaction.Transactional;

@Service
public class PaymentService 
{
	@Autowired
	PaymentRepository paymentRepository;
	
	public String checkMessage()
	{
		return "Working Fine";
	}
	
	public Payment addTransaction(Payment transaction)
	{
		return paymentRepository.save(transaction);
	}
	
	public List<Payment> viewAllTransactions()
	{
		return paymentRepository.findAll();
	}
	
	public Payment viewTransactionById(int transactionId)
	{
		return paymentRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found with ID: "+transactionId));
	
	}
	
	@Transactional
	public Payment updateTransaction(int transactionId,Payment updatedTransaction)
	{
		Payment existingTransaction = paymentRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found with ID: "+transactionId));
			
		if(updatedTransaction.getAmountPaid() != 0)
		{			
			existingTransaction.setAmountPaid(updatedTransaction.getAmountPaid());
		}
		if(updatedTransaction.getPaymentMethod() != null)
		{			
			existingTransaction.setPaymentMethod(updatedTransaction.getPaymentMethod());
		}
		if(updatedTransaction.getPaymentStatus() != null)
		{			
			existingTransaction.setPaymentStatus(updatedTransaction.getPaymentStatus());
		}
		
		return paymentRepository.save(existingTransaction);
	}
	
	public String deleteTransaction(int transactionId)
	{
		if(!paymentRepository.existsById(transactionId))
		{
			throw new RuntimeException("Transaction not found with ID: "+transactionId);
		}
		
		paymentRepository.deleteById(transactionId);
		
		return "Transaction with ID "+transactionId+" has been deleted successfully!";
	}
	
	public List<Payment> viewTransactionsByPaymentMethod(String paymentMethod)
	{
		return paymentRepository.findByPaymentMethod(paymentMethod);
	}
	
	public List<Payment> getTransactionsByStatus(String status) {
        return paymentRepository.findByPaymentStatus(status);
    }
	
	
	// Fetch transactions by date
    public List<Payment> getTransactionsByDate(String date) {
        // Parse the date string into LocalDate and generate start and end LocalDateTime for the day
        LocalDate localDate = LocalDate.parse(date);  // Assumes the date is in "yyyy-MM-dd" format
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);

        // Use repository method to fetch transactions within the date range
        return paymentRepository.findByTransactionDateBetween(startOfDay, endOfDay);
    }
    
    
    public PaymentSummaryDTO generateDailyPaymentSummaryReport(LocalDate date) {
        double cashTotal = paymentRepository.sumAmountByPaymentMethodAndDate("Cash", date);
        double creditCardTotal = paymentRepository.sumAmountByPaymentMethodAndDate("Credit Card", date);
        double debitCardTotal = paymentRepository.sumAmountByPaymentMethodAndDate("Debit Card", date);
        double upiTotal = paymentRepository.sumAmountByPaymentMethodAndDate("UPI", date);
        double totalAmount = cashTotal + creditCardTotal + debitCardTotal + upiTotal;

        return new PaymentSummaryDTO(date, cashTotal, creditCardTotal, debitCardTotal, upiTotal, totalAmount);
    }
    
    public List<Payment> getCompletedTransactions(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
        
        return paymentRepository.findByTransactionDateBetweenAndPaymentStatus(startOfDay, endOfDay, "Completed");
    }
    
    public List<Payment> getTransactionsBetweenDates(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
        return paymentRepository.findByTransactionDateBetween(startOfDay, endOfDay);
    }
    
    
    
}











