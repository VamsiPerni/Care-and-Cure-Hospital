package com.cac.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cac.entity.Appointment;
import com.cac.service.AppointmentService;
import com.cac.service.EmailService;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
public class AppointmentController {
	@Autowired
	AppointmentService appointmentService;
	@Autowired
	private EmailService emailService;

	@GetMapping("/viewAppointmentsByDoctor/{doctorId}/{date}")
	public List<Appointment> getAppointmentsByDoctor(@PathVariable Integer doctorId,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return appointmentService.viewAppointmentsByDoctor(doctorId, date);
	}

	@GetMapping("/viewAppointmentsByPatient/{patientId}")
	public Appointment getAppointmentByPatient(@PathVariable Integer patientId) {
		List<Appointment> appointments = appointmentService.viewAppointmentsByPatient(patientId);
		return appointments.isEmpty() ? null : appointments.get(0); // Return the first appointment
	}

//
	@PostMapping("/addAppointment")
	public ResponseEntity<?> addAppointment(@RequestBody Appointment appointment) {
	    try {
	        System.out.println("Received appointment: " + appointment);
	        Appointment savedAppointment = appointmentService.addAppointment(appointment);

	        // **Fetch patient's email from the Patient entity**
//	        if (savedAppointment != null && savedAppointment.getPatient() != null) {
//	            String patientEmail = savedAppointment.getPatient().getEmailId();
//	            if (patientEmail != null && !patientEmail.isEmpty()) {
//	                emailService.sendAppointmentConfirmation(patientEmail, savedAppointment);
//	            }
//	        }

	        return ResponseEntity.ok(Map.of("success", true, "appointment", savedAppointment));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("success", false, "message", e.getMessage()));
	    }
	}

	
    

	@GetMapping("/viewAllAppointment")
	public ResponseEntity<List<Appointment>> viewAllAppointment() {
		 List<Appointment> appointments = appointmentService.viewAllAppointment()
		            .stream()
		            .sorted(Comparator.comparing(Appointment::getAppointmentDate)
		                    .thenComparing(Appointment::getSlot)
		                    .reversed()) // Sort in Descending Order
		            .collect(Collectors.toList());

		    return new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	@GetMapping("/viewAppointmentById/{id}")
	public ResponseEntity<Appointment> viewAppointmentById(@PathVariable Integer id) {
		Appointment appointment = appointmentService.viewAppointmentById(id);
		return (appointment != null) ? new ResponseEntity<>(appointment, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/viewAppointmentsByDate")
	public ResponseEntity<List<Appointment>> viewAppointmentsByDate(
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		if (date == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		List<Appointment> appointments = appointmentService.viewAppointmentsByDate(date);

		if (appointments == null || appointments.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	@GetMapping("/viewCancelledAppointments")
	public ResponseEntity<?> viewCancelledAppointments(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		if (date == null) {
			return ResponseEntity.badRequest().body("Date parameter is required.");
		}

		List<Appointment> cancelledAppointments = appointmentService.viewCancelledAppointments(date);
		return ResponseEntity.ok(cancelledAppointments);
	}

	@GetMapping("/report/healthIssue/{healthIssue}/{startDate}/{endDate}")
	public ResponseEntity<List<Appointment>> generateHealthIssueAppointmentReport(@PathVariable String healthIssue,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		List<Appointment> report = appointmentService.generateHealthIssueAppointmentReport(healthIssue, startDate,
				endDate);
		return new ResponseEntity<>(report, HttpStatus.OK);
	}

	@GetMapping("/report/cancellation/{startDate}/{endDate}")
	public ResponseEntity<List<Appointment>> generateAppointmentCancellationReport(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		List<Appointment> report = appointmentService.generateAppointmentCancellationReport(startDate, endDate);
		return new ResponseEntity<>(report, HttpStatus.OK);
	}

	@PutMapping("/cancelAppointment/{id}")
	public ResponseEntity<String> cancelAppointment(@PathVariable Integer id) {
		try {
			appointmentService.cancelAppointment(id);
			return new ResponseEntity<>("Appointment cancelled successfully", HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/rescheduleAppointment/{id}")
	public ResponseEntity<Appointment> rescheduleAppointment(@PathVariable Integer id,
			@RequestBody Appointment appointment) {
		Appointment rescheduledAppointment = appointmentService.rescheduleAppointment(id, appointment);
		if (rescheduledAppointment != null) {
			return new ResponseEntity<>(rescheduledAppointment, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(rescheduledAppointment, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/viewTodayAppointments")
	public ResponseEntity<List<Appointment>> viewTodayAppointments() {
		LocalDate today = LocalDate.now();
		List<Appointment> appointments = appointmentService.viewAppointmentsByDate(today);

		return appointments.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	@GetMapping("/appointmentCounts")
	public ResponseEntity<List<Map<String, Object>>> getAllAppointmentCounts() {
	    List<Map<String, Object>> appointmentCounts = appointmentService.getAllAppointmentCounts();
	    return ResponseEntity.ok(appointmentCounts);
	}

	
	@GetMapping("/appointments/statistics")
	public ResponseEntity<List<Map<String, Object>>> getMonthlyAppointmentStats() {
	    List<Map<String, Object>> statistics = appointmentService.getAppointmentsByMonth();
	    return ResponseEntity.ok(statistics);
	}

	    // Fetch the ongoing appointment
	@GetMapping("/ongoing")
	public ResponseEntity<Appointment> getOngoingAppointment() {
	    Appointment ongoingAppointment = appointmentService.getOngoingAppointment();
	    if (ongoingAppointment != null) {
	        System.out.println("Ongoing appointment found: " + ongoingAppointment);
	        return ResponseEntity.ok(ongoingAppointment);
	    }
	    System.out.println("No ongoing appointments found.");
	    return ResponseEntity.noContent().build();
	}


	 @PutMapping("/{id}/complete")
	    public ResponseEntity<String> markAppointmentAsCompleted(@PathVariable Integer id) {
	        return updateAppointmentStatus(id, "Completed");
	    }

	    @PutMapping("/{id}/cancel")
	    public ResponseEntity<String> markAppointmentAsCanceled(@PathVariable Integer id) {
	        return updateAppointmentStatus(id, "Cancelled");
	    }

	    private ResponseEntity<String> updateAppointmentStatus(Integer id, String status) {
	        Optional<Appointment> optionalAppointment = appointmentService.findById(id);

	        if (optionalAppointment.isPresent()) {
	            appointmentService.updateAppointmentStatus(id, status);
	            return ResponseEntity.ok("Appointment status updated to " + status);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
	        }
	    }
	}



