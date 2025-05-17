package com.cac.controller;

import com.cac.entity.Doctor;
import com.cac.entity.Patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/ui/patients")
public class PatientController {

	@Autowired
    private RestTemplate restTemplate;

    // Read the base URL from application.properties:
    // e.g. "http://localhost:8080/patients"
    private final String backendUrl="http://localhost:8080/patients";

//    public PatientController(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
    
    @GetMapping("/")
    public String home() {
        // "home" corresponds to a Thymeleaf template named "home.html"
        return "patienthome";
    }

    @GetMapping("/patientDashboard")
    public String homeDashboard() {
        // "home" corresponds to a Thymeleaf template named "home.html"
        return "patientDashboard";
    }

    // ------------------------------------------------
    // 1. Show "Add Patient" form
    // ------------------------------------------------
    @GetMapping("/add")
    public String showAddPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "add-patient"; // Thymeleaf template
    }


    @GetMapping("/addPD")
    public String showAddPatientFormPD(Model model) {
        model.addAttribute("patient", new Patient());
        return "addPD"; // Thymeleaf template
    }

    // @GetMapping("/addPD")
    // public String showAddPatientFormPD(Model model) {
    //     model.addAttribute("patient", new Patient());
    //     return "addPD";
    // }


    // ------------------------------------------------
    // 2. Handle "Add Patient" form submission
    // ------------------------------------------------
    @PostMapping("/save")
    public String saveNewPatient(@ModelAttribute("patient") Patient patient, Model model) {
        try {
            // Typically, to create a new patient, we POST to /patients (the base URL)
            String url = backendUrl+ "/add";; // e.g. "http://localhost:8080/patients"
            Patient created = restTemplate.postForObject(url, patient, Patient.class);

            // Redirect to list page on success
            return "redirect:/ui/patients/all";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "add-patient";
        }
    }

    // ------------------------------------------------
    // 3. List All Patients
    // ------------------------------------------------
    @GetMapping("/all")
    public String listAllPatients(Model model) {
        // e.g. "http://localhost:8080/patients/all"
        String url = backendUrl + "/all";
        ResponseEntity<Patient[]> response = restTemplate.getForEntity(url, Patient[].class);
        Patient[] patientArray = response.getBody();
        List<Patient> patients = (patientArray != null) ? Arrays.asList(patientArray) : List.of();
        model.addAttribute("patients", patients);
        return "patients-list";
    }

    // ------------------------------------------------
    // 4. Filter Patients by Status
    //     e.g. /ui/patients/status/active or /ui/patients/status/inactive
    // ------------------------------------------------
    @GetMapping("/status/{status}")
    public String listPatientsByStatus(@PathVariable("status") String status, Model model) {
        // e.g. "http://localhost:8080/patients/status/active"
        String url = backendUrl + "/status/" + status;
        ResponseEntity<Patient[]> response = restTemplate.getForEntity(url, Patient[].class);
        Patient[] patientArray = response.getBody();
        List<Patient> patients = (patientArray != null) ? Arrays.asList(patientArray) : List.of();
        model.addAttribute("patients", patients);
        return "patients-by-status";
    }

    // ------------------------------------------------
    // 5. View a Single Patient
    //     e.g. /ui/patients/5
    // ------------------------------------------------
    @GetMapping("/enterPatientId")
    public String enterPatientId(){
    	return "enterPatientId";
    }

    
    @GetMapping("/view")
    public String viewPatient(@RequestParam("id") int id, Model model) {
    	try {
			ResponseEntity<Patient> response=restTemplate.getForEntity(
			backendUrl + "/view/" + id, Patient.class);
			model.addAttribute("patient", response.getBody());
			return "patient-profile";
			
		}
		catch(Exception e) {
			
			e.printStackTrace();
			model.addAttribute("error", "Error fetching patient details.");
		    return "patient-profile";
		}
    }


    // ------------------------------------------------
    // 6. Show Update Form
    //     e.g. /ui/patients/update/5
    // ------------------------------------------------
    
    @GetMapping("/enterId")
    public String enterId(){
    	return "enterId";
    }
    
    @GetMapping("/update")
    public String updateDoctorForm(@RequestParam("id") int patientId, Model model) {
        try {
            ResponseEntity<Patient> response = restTemplate.getForEntity(backendUrl + "/view/" + patientId, Patient.class);
            model.addAttribute("patient", response.getBody());
            return "update-patient"; // Renders updateDoctor.html
        } catch (Exception e) {
            model.addAttribute("error", "Patient not found with ID: " + patientId);
            return "enterId"; // Redirects back to enterDoctorId.html with an error
        }
    }

    // Step 3: Update Doctor details
    @PutMapping("/update")
    public String updateDoctor(@RequestParam("id") int patientId, @ModelAttribute Patient patient, Model model) {
        try {
            restTemplate.put(backendUrl + "/update/" + patientId, patient);
            System.out.println(patient);
            model.addAttribute("message", "Patient updated successfully: " + patient.getPatientName());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "update-patient"; // Renders updateMessage.html with success/error message
    }


    // ------------------------------------------------
    // 8. Show Patients by Doctor
    //     e.g. /ui/patients/doctor?doctorId=10
    // ------------------------------------------------
    @GetMapping("/doctor")
    public String getPatientsByDoctor(@RequestParam(value = "doctorId", required = false) Integer doctorId,
                                      Model model) {
        if (doctorId != null) {
            // e.g. "http://localhost:8080/patients/doctor/10"
            String url = backendUrl + "/doctor/" + doctorId;
            ResponseEntity<Patient[]> response = restTemplate.getForEntity(url, Patient[].class);
            Patient[] patientArray = response.getBody();
            List<Patient> patients = (patientArray != null) ? Arrays.asList(patientArray) : List.of();
            model.addAttribute("patients", patients);
        }
        return "patients-by-doctor";
    }

    // ------------------------------------------------
    // 9. Reports
    //     (a) Daily Appointments
    // ------------------------------------------------
    @GetMapping("/report/dailyAppointments")
    public String dailyAppointmentReport(@RequestParam(value = "date", required = false) String date,
                                         Model model) {
        if (date == null || date.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please provide a valid date.");
            return "dailyAppointmentReport";
        }
        try {
            // Construct the URL with the date as a path variable
            String url = backendUrl + "/report/dailyAppointments/" + date;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("dailyReport", response.getBody());
            } else {
                model.addAttribute("errorMessage", "Failed to fetch report. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to fetch the daily appointments report. Please try again later.");
        }
        return "dailyAppointmentReport";
    }



    @GetMapping("/report/noShows")
    public String noShowReport(@RequestParam(value = "startDate", required = false) String startDate,
                               @RequestParam(value = "endDate", required = false) String endDate,
                               Model model) {
        if (startDate == null || endDate == null || startDate.trim().isEmpty() || endDate.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please provide both start and end dates.");
            return "noShowReport";
        }
        try {
            // e.g. "http://localhost:8080/patients/report/noShows?startDate=2025-01-01&endDate=2025-01-31"
            String url = backendUrl + "/report/noShows?startDate=" + startDate + "&endDate=" + endDate;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("noShowReport", response.getBody());
            } else {
                model.addAttribute("errorMessage", "Failed to fetch no-show report. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to fetch no-show report. Please try again later.");
        }
        return "noShowReport";
    }

    @GetMapping("/report/healthIssue")
    public String patientsByHealthIssue(@RequestParam(value = "issue", required = false) String issue,
                                        Model model) {
        if (issue == null || issue.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please provide a health issue.");
            return "patientsByHealthIssue";
        }
        try {
            // Backend endpoint expects the issue as a path variable.
            // e.g. "http://localhost:8080/patients/report/healthIssue/Flu"
            String url = backendUrl + "/report/healthIssue/" + issue;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("healthIssueReport", response.getBody());
            } else {
                model.addAttribute("errorMessage", "Failed to fetch health issue report. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to fetch health issue report. Please try again later.");
        }
        return "patientsByHealthIssue";
    }



    // ------------------------------------------------
    // 10. Deactivate a Patient
    //     e.g. /ui/patients/deactivate/5
    // ------------------------------------------------
    @GetMapping("/deactivate/{id}")
    public String deactivatePatient(@PathVariable("id") int id) {
        try {
            // e.g. "http://localhost:8080/patients/5/deactivate"
            String url = backendUrl + "/" + id + "/deactivate";
            restTemplate.put(url, null);
        } catch (Exception e) {
            // handle error if needed
        }
        return "redirect:/ui/patients";
    }
}
