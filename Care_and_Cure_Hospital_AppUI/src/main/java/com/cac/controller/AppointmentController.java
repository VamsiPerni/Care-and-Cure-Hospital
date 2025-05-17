package com.cac.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cac.entity.Appointment;

@Controller
public class AppointmentController {
    private final String BACKEND_URL = "http://localhost:8080"; 
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/appHome")
    public String homePage() {
        return "appointmenthome";
    }

    @GetMapping("/addAppointment")
    public String showAddAppointmentForm(Model model) {
    	
        model.addAttribute("appointment", new Appointment());
        return "addAppointment";
    }

    @PostMapping("/addAppointment")
    public String addAppointment(@ModelAttribute Appointment appointment, RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(BACKEND_URL + "/addAppointment", appointment, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && Boolean.TRUE.equals(response.getBody().get("success"))) {
                redirectAttributes.addFlashAttribute("message", "Appointment added successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to add appointment: " + response.getBody().get("message"));
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error occurred: " + e.getMessage());
        }
        
        return "redirect:/viewAllAppointment";
    }


    @GetMapping("/viewAllAppointment")
    public String viewAllAppointment(Model model) {
        ResponseEntity<Appointment[]> response = restTemplate.getForEntity(BACKEND_URL + "/viewAllAppointment", Appointment[].class);
        List<Appointment> appointments = Arrays.asList(response.getBody());
        model.addAttribute("appointments", appointments);
        return "viewAllAppointment";
    }

    @GetMapping("/viewAppointmentById")
    public String showSearchAppointmentPage() {
        return "viewAppointmentById";  // Shows the search form page
    }

    
    @GetMapping("/viewAppointmentById/{id}")
    public String viewAppointmentById(@PathVariable Integer id, Model model) {
        try {
            ResponseEntity<Appointment> response = restTemplate.getForEntity(BACKEND_URL + "/viewAppointmentById/" + id, Appointment.class);
            model.addAttribute("appointment", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Appointment not found.");
        }
        return "viewAppointmentById";
    }
    



    @GetMapping("/viewAppointmentsByDate")
    public String viewAppointmentsByDate(@RequestParam(required = false) String date, Model model) {
        if (date == null || date.isEmpty()) {
            model.addAttribute("error", "Date parameter is required.");
            return "viewAppointmentsByDate";  
        }

        ResponseEntity<Appointment[]> response = restTemplate.getForEntity(BACKEND_URL + "/viewAppointmentsByDate/" + date, Appointment[].class);
        model.addAttribute("appointments", Arrays.asList(response.getBody()));
        return "viewAppointmentsByDate";
    }

    
    @GetMapping("/viewAppointment")
    public String viewAppointment(@RequestParam("appointmentId") Integer appointmentId, Model model) {
        ResponseEntity<Appointment> response = restTemplate.getForEntity(BACKEND_URL + "/viewAppointmentById/" + appointmentId, Appointment.class);
        model.addAttribute("appointment", response.getBody());
        return "viewAppointment";
    }

    @GetMapping("/cancelAppointment/{id}")
    public String cancelAppointment(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        restTemplate.put(BACKEND_URL + "/cancelAppointment/" + id, null);
        redirectAttributes.addFlashAttribute("message", "Appointment canceled successfully!");
        return "redirect:/viewAllAppointment";
    }

    @GetMapping("/rescheduleAppointmentForm/{id}")
    public String showRescheduleForm(@PathVariable Integer id, Model model) {
        ResponseEntity<Appointment> response = restTemplate.getForEntity(BACKEND_URL + "/viewAppointmentById/" + id, Appointment.class);
        model.addAttribute("appointment", response.getBody());
        return "rescheduleAppointment";
    }

    @PostMapping("/rescheduleAppointment")
    public String rescheduleAppointmentForm(@ModelAttribute Appointment appointment, RedirectAttributes redirectAttributes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Appointment> request = new HttpEntity<>(appointment, headers);
        ResponseEntity<Appointment> response = restTemplate.exchange(
            BACKEND_URL + "/rescheduleAppointment/" + appointment.getAppointmentId(),  // Fixed URL formatting
            HttpMethod.PUT,
            request,
            Appointment.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            redirectAttributes.addFlashAttribute("message", "Appointment rescheduled successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to reschedule appointment.");
        }
        
        return "redirect:/viewAllAppointment";
    }
    
    
    @GetMapping("/viewCancelledAppointments")
    public String viewCancelledAppointments(@RequestParam(required = false) String date, Model model) {
        model.addAttribute("selectedDate", date);
        return "viewCancelledAppointments"; // Thymeleaf template name (viewCancelledAppointments.html)
    }
    
    @GetMapping("/viewAppointmentsByPatient")
    public String showSearchAppointmentByPatientPage() {
        return "viewAppointmentsByPatient";  // Shows the search form page
    }
    
    @GetMapping("/viewAppointmentsByPatient/{patientId}")
    public String viewAppointmentsByPatient(@PathVariable("patientId") Integer patientId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Appointment[] appointmentsArray = restTemplate.getForObject(BACKEND_URL + "/viewAppointmentsByPatient/" + patientId, Appointment[].class);
            List<Appointment> appointments = Arrays.asList(appointmentsArray);
            model.addAttribute("appointments", appointments);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to fetch appointments.");
             // Redirect to a safe page if fetching fails
        }
        return "viewAppointmentsByPatient"; // Matches Thymeleaf template
    }
    
    @GetMapping("/viewAppointmentsByDoctor")
    public String showViewAppoitnmentsByDoctor() {
    	return "viewAppointmentsByDoctor";
    }
    
    
    @GetMapping("/viewAppointmentsByDoctor/{doctorId}/{date}")
    public String viewAppointmentsByDoctor(@PathVariable("doctorId") Integer doctorId,
                                           @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        try {
            Appointment[] appointmentsArray = restTemplate.getForObject(
                BACKEND_URL + "/viewAppointmentsByDoctor/" + doctorId + "/" + date, 
                Appointment[].class
            );
            List<Appointment> appointments = Arrays.asList(appointmentsArray);
            model.addAttribute("appointments", appointments);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to fetch appointments for Doctor.");
            return "redirect:/errorPage"; // Redirect to an error handling page
        }
        return "viewAppointmentsByDoctor"; // Matches Thymeleaf template
    }
    
//    @GetMapping("/viewAppointmentsByDoctor")
//    public String viewAppointmentsByDoctor(@RequestParam("doctorId") Integer doctorId, 
//                                           @RequestParam("appointmentDate") String date, 
//                                           Model model, 
//                                           RedirectAttributes redirectAttributes) {
//        try {
//            // Make a REST API call to fetch appointments for the given doctor and date
//            Appointment[] appointmentsArray = restTemplate.getForObject(BACKEND_URL + "/viewAppointmentsByDoctor/" + doctorId + "/" + date, Appointment[].class);
//
//            // Convert the array of appointments to a List
//            List<Appointment> appointments = Arrays.asList(appointmentsArray);
//
//            // Add appointments to the model to be displayed in the view
//            model.addAttribute("appointments", appointments);
//        } catch (Exception e) {
//            // In case of an error (e.g., backend down, incorrect doctor ID, etc.)
//            redirectAttributes.addFlashAttribute("error", "Failed to fetch appointments.");
//            return "redirect:/"; // Redirect to a safe page (home page or error page)
//        }
//        return "viewAppointmentsByDoctor"; // Thymeleaf template name
//    }
    
    
    




}
