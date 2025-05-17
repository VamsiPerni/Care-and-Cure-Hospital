package com.cac.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cac.entity.Appointment;
import com.cac.entity.Doctor;

import jakarta.transaction.Transactional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
	
	@Query("SELECT d FROM Doctor d WHERE LOWER(REPLACE(d.specialization, ' ', '')) = LOWER(REPLACE(:specialization, ' ', ''))")
    List<Doctor> findBySpecializationIgnoreCase(@Param("specialization") String specialization);
	
	@Query("SELECT d FROM Doctor d WHERE d.status = true")
	List<Doctor> findAllActiveDoctors();
	
	@Query("SELECT d FROM Doctor d WHERE :slot MEMBER OF d.availableSlots")
    List<Doctor> findByAvailabilitySlot(LocalDateTime slot);

    // Find doctors available on a specific day
    @Query("SELECT d FROM Doctor d WHERE EXISTS (SELECT a FROM d.availableSlots a WHERE FUNCTION('DATE', a) = :day)")
    List<Doctor> findByAvailabilityDay(LocalDate day);
    
    @Transactional
    @Modifying
    @Query("UPDATE Doctor d SET d.status = false WHERE d.doctorId = :doctorId AND d.status = true")
    int deactivateDoctor(int doctorId);

    // Find doctors with consultation count between given dates
    
    @Query("SELECT d FROM Doctor d " +
            "JOIN d.appointmentList a " +
            "WHERE a.appointmentDate BETWEEN :startDate AND :endDate " +
            "AND a.status <> 'Cancelled' " +
            "GROUP BY d " +
            "HAVING COUNT(a) >= :minCount")
     List<Doctor> findByConsultationCount(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate, 
                                                 @Param("minCount") int minCount);

    
    //generate doctor appointment report
    
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.appointmentDate ASC")
     List<Appointment> generateDoctorAppointmentReport(@Param("doctorId") int doctorId, 
                                                       @Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate);
 
    
     //generate doctor revenue report   
    
    @Query("SELECT SUM(d.consultationFees) FROM Appointment a " +
            "JOIN a.doctor d " +
            "WHERE d.doctorId = :doctorId " +
            "AND a.appointmentDate BETWEEN :startDate AND :endDate " +
            "AND a.status <> 'Cancelled'")
     Double generateDoctorRevenueReport(@Param("doctorId") int doctorId, 
                                        @Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
  
    
    
    // generate low consultation doctors report
    
    @Query("""
    	       SELECT d FROM Doctor d 
    	       WHERE (SELECT COUNT(a) FROM Appointment a 
    	              WHERE a.doctor = d 
    	              AND a.appointmentDate BETWEEN :startDate AND :endDate 
    	              AND a.status <> 'Cancelled') 
    	              <= (SELECT COUNT(a2) * 0.3 FROM Appointment a2 
    	                  WHERE a2.appointmentDate BETWEEN :startDate AND :endDate 
    	                  AND a2.status <> 'Cancelled')
    	       """)
    	List<Doctor> generateLowConsultationDoctorsReport(@Param("startDate") LocalDate startDate, 
    	                                                   @Param("endDate") LocalDate endDate);   
    
    //get most frequently booked doctors
    
    @Query("""
    	    SELECT d FROM Doctor d 
    	    WHERE (SELECT COUNT(a) FROM Appointment a 
    	           WHERE a.doctor = d 
    	           AND a.appointmentDate BETWEEN :startDate AND :endDate 
    	           AND a.status <> 'Cancelled') 
    	           >= (SELECT COUNT(a2) * 0.7 FROM Appointment a2 
    	               WHERE a2.appointmentDate BETWEEN :startDate AND :endDate 
    	               AND a2.status <> 'Cancelled')
    	""")
    	List<Doctor> generateMostFrequentlyBookedDoctorsReport(@Param("startDate") LocalDate startDate, 
    	                                                       @Param("endDate") LocalDate endDate);
    
    
    // Find available doctors for a given date
    @Query("SELECT d FROM Doctor d WHERE EXISTS (SELECT s FROM d.availableSlots s WHERE DATE(s) = :date)")
    List<Doctor> findDoctorAvailability(@Param("date") LocalDate date);
    
    
//    Doctor findByDoctorIdAndPassword(Integer doctorId, String password);
    
    @Query("SELECT COUNT(DISTINCT p.patientId) FROM Appointment a JOIN a.patient p WHERE a.doctor.doctorId = :doctorId AND a.status <> 'Cancelled'")
    Long countTotalPatients(Integer doctorId);
    
    @Query("SELECT COUNT(a) FROM Appointment a " +
    	       "WHERE a.doctor.doctorId = :doctorId AND a.status <> 'Cancelled'")
    	Long countTotalAppointments(@Param("doctorId") Integer doctorId);

    
    @Query(value = "SELECT COUNT(*) * d.consultation_fees " +
            "FROM appointment a " +
            "JOIN doctor d ON a.doctor_id = d.doctor_id " +
            "WHERE a.doctor_id = :doctorId AND a.status <> 'Cancelled'", 
    nativeQuery = true)
    Double calculateTotalRevenue(@Param("doctorId") Integer doctorId);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId AND DATE(a.appointmentDate) = CURRENT_DATE")
    List<Appointment> findTodayAppointments(Integer doctorId);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId AND a.appointmentDate >= CURRENT_DATE")
    List<Appointment> findUpcomingAppointments(Integer doctorId);
    
    @Query("SELECT p.gender, COUNT(p) FROM Appointment a JOIN a.patient p " +
           "WHERE a.doctor.doctorId = :doctorId GROUP BY p.gender")
    List<Object[]> getPatientGenderDistribution(Integer doctorId);
    
    @Query("SELECT FUNCTION('MONTHNAME', a.appointmentDate) AS month, COUNT(a) AS appointments " +
           "FROM Appointment a WHERE a.doctor.doctorId = :doctorId AND YEAR(a.appointmentDate) = YEAR(CURRENT_DATE) " +
           "GROUP BY FUNCTION('MONTH', a.appointmentDate), FUNCTION('MONTHNAME', a.appointmentDate)")
    List<Object[]> getMonthlyAppointments(Integer doctorId);
    
    @Query("SELECT FUNCTION('MONTHNAME', a.appointmentDate) AS month, " +
    	       "SUM(d.consultationFees) AS revenue " +
    	       "FROM Appointment a " +
    	       "JOIN a.doctor d " +
    	       "WHERE a.doctor.doctorId = :doctorId " +
    	       "AND YEAR(a.appointmentDate) = YEAR(CURRENT_DATE) " +
    	       "AND a.status <> 'Cancelled' "+
    	       "GROUP BY FUNCTION('MONTH', a.appointmentDate), FUNCTION('MONTHNAME', a.appointmentDate)")
    	List<Object[]> getMonthlyRevenue(@Param("doctorId") Integer doctorId);

}

