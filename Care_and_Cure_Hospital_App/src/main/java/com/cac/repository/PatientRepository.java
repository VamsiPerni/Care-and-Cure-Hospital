package com.cac.repository;

import com.cac.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

    Optional<Patient> findByEmailId(String emailId);

    Optional<Patient> findByContactNumber(String contactNumber);

    // For scheduled deactivation: find active patients whose last visit date < cutoff
    @Query("SELECT p FROM Patient p WHERE p.status = true AND p.lastVisitDate < :cutoffDate")
    List<Patient> findInactivePatients(@Param("cutoffDate") LocalDate cutoffDate);

    // Find by status
    List<Patient> findByStatus(boolean status);

    // Find by assigned doctor
    List<Patient> findByDoctorId(int doctorId);

    // For health-issue-based searching in medical history
    @Query("SELECT p FROM Patient p WHERE LOWER(p.medicalHistory) LIKE LOWER(CONCAT('%', :issue, '%'))")
    List<Patient> findByMedicalHistoryContaining(@Param("issue") String issue);
}
