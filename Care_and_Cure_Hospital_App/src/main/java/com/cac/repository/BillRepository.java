package com.cac.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cac.entity.Appointment;
import com.cac.entity.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {

    // Find Bill by Appointment
    Optional<Bill> findByAppObj(Appointment appObj);



    @Query("SELECT b FROM Bill b JOIN b.appObj a WHERE a.patient.patientId = :patientId")
    List<Bill> findBillsByPatientId(@Param("patientId") int patientId);

    Optional<Bill> findById(int billId);

   

//    // Fetch Unpaid Bills

    // Fetch Bills with Discount
    @Query("SELECT b FROM Bill b WHERE b.discountPercentage > 0")
    List<Bill> findBillsWithDiscount();
    
    @Query("SELECT b FROM Bill b WHERE b.billDate = :date")
    List<Bill> findByBillDate(@Param("date") LocalDate date);
    
    
    @Query("""
    	    SELECT b FROM Bill b 
    	    LEFT JOIN Payment p ON p.billObj = b 
    	    WHERE p.paymentStatus IS NULL OR p.paymentStatus = 'pending'
    	""")
    	List<Bill> findUnpaidBills();
   
}