package com.cac.controller;

import com.cac.entity.Doctor;
import com.cac.entity.Patient;
import com.cac.exception.InvalidEntityException;
import com.cac.service.PatientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientContoller {
	

	@Autowired
    private PatientService patientService;

//    public PatientContoller(PatientService patientService) {
//        this.patientService = patientService;
//    }

    // 1. Register a new patient
    @PostMapping("/add")
    public ResponseEntity<?> addPatient(@RequestBody Patient patient) {
        try {
            Patient savedPatient = patientService.addPatient(patient);
            return ResponseEntity.ok(savedPatient);
        } catch (InvalidEntityException e) {
            // Return 400 Bad Request if the entity is invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 2. Get patient by ID
    @GetMapping("/view/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable int id) {
        // If not found, PatientNotFoundException is thrown -> handled by GlobalExceptionHandler
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    // 3. Update patient profile
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable int id, @RequestBody Patient updated) {
        try {
            Patient savedPatient = patientService.updatePatient(id, updated);
            return ResponseEntity.ok(savedPatient);
        } catch (InvalidEntityException e) {
            // Return 400 Bad Request if the entity is invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 4. Deactivate patient
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivatePatient(@PathVariable int id) {
        patientService.deactivatePatient(id);
        return ResponseEntity.ok("Patient deactivated successfully");
    }

    // 5. View patients by status (active/inactive)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Patient>> getPatientsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(patientService.getPatientsByStatus(status));
    }

    // 6. View patients by assigned doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Patient>> getPatientsByDoctor(@PathVariable int doctorId) {
        return ResponseEntity.ok(patientService.getPatientsByDoctor(doctorId));
    }

    // 7. View patient medical history
    @GetMapping("/{id}/medicalHistory")
    public ResponseEntity<String> getPatientMedicalHistory(@PathVariable int id) {
        return ResponseEntity.ok(patientService.getMedicalHistory(id));
    }

    // 8. Generate daily appointment report
    @GetMapping("/report/dailyAppointments/{date}")
    public ResponseEntity<String> generateDailyAppointmentReport(@PathVariable LocalDate date) {
        return ResponseEntity.ok(patientService.generateDailyAppointmentReport(date));
    }

    // 9. Generate patient no-show report
    @GetMapping("/report/noShows")
    public ResponseEntity<String> generateNoShowReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(patientService.generateNoShowReport(startDate, endDate));
    }

    // 10. Generate patients by health issue report
    @GetMapping("/report/healthIssue/{issue}")
    public ResponseEntity<String> generatePatientsByHealthIssueReport(@PathVariable String issue) {
        return ResponseEntity.ok(patientService.generatePatientsByHealthIssueReport(issue));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Patient>> getAllPatients() {
        // If you want both active + inactive, you can do:
        return ResponseEntity.ok(patientService.getPatientsByStatus("active")); 
        // Or create a new service method getAllPatients() that calls patientRepository.findAll().
    }
}
