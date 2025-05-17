package com.cac.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cac.entity.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
	boolean existsByAppointmentDateAndSlot(LocalDate date, String slot);
	@EntityGraph(attributePaths = {"doctor", "doctor.doctorName", "patient"})
	List<Appointment> findByAppointmentDate(LocalDate date);

	List<Appointment> findByAppointmentDateAndStatus(LocalDate date, String status);

	List<Appointment> findByReasonForVisitAndAppointmentDateBetween(String healthIssue, LocalDate startDate,
			LocalDate endDate);

	List<Appointment> findByStatusAndAppointmentDateBetween(String status, LocalDate startDate, LocalDate endDate);

	List<Appointment> findByDoctor_DoctorIdAndAppointmentDate(Integer doctorId, LocalDate appointmentDate);

	List<Appointment> findByPatient_PatientId(Integer patientId);

	List<Appointment> findByPatient_PatientIdAndAppointmentDateAfter(Integer patientId, LocalDate appointmentDate);

	int countByStatusAndAppointmentDate(@Param("status") String status, @Param("appointmentDate") LocalDate date);
	
	@Query("SELECT FUNCTION('MONTH', a.appointmentDate), " +
		       "FUNCTION('MONTHNAME', a.appointmentDate), " +
		       "COUNT(a) " +
		       "FROM Appointment a " +
		       "GROUP BY FUNCTION('MONTH', a.appointmentDate), FUNCTION('MONTHNAME', a.appointmentDate) " +
		       "ORDER BY FUNCTION('MONTH', a.appointmentDate)")
		List<Object[]> countAppointmentsByMonth();



		@Query("SELECT DISTINCT a.appointmentDate FROM Appointment a ORDER BY a.appointmentDate")
		List<LocalDate> findDistinctAppointmentDates();
		
		Optional<Appointment> findByAppointmentId(Integer appointmentId);

		@Query("SELECT a FROM Appointment a WHERE a.status = 'Scheduled' AND a.appointmentDate = CURRENT_DATE ORDER BY a.slot ASC")
		Optional<Appointment> findNextOngoingAppointment();
		
		
		//================for bill summary ===============
		
		 @Query("SELECT a FROM Appointment a WHERE a.patient.patientId = :patientId")
		    List<Appointment> findByPatientId(@Param("patientId") String patientId);

		    @Query("SELECT a FROM Appointment a WHERE a.patient.patientId = :patientId")
		    List<Appointment> findAppointmentsByPatientId(@Param("patientId") int patientId);

		    @Query("SELECT a FROM Appointment a WHERE a.patient.patientId = :patientId AND a.appointmentDate >= CURRENT_DATE")
		    List<Appointment> findUpcomingAppointmentsByPatientId(@Param("patientId") int patientId);

		
//
	//List<Appointment> findByAppointmentDate(@Param("appointmentDate") LocalDate date);

}