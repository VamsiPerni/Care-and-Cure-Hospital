package com.cac.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cac.entity.Appointment;
import com.cac.entity.Doctor;


@Controller
public class DoctorController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080"; // Replace with actual URL
    
    @GetMapping("/dashboard")
    public String dashboard() {
    	return "fragments/dashboard";
    }
    
    @GetMapping("/doctor")
	public String layout() {
		return "layout";
	}
    
    @GetMapping("/docHome")
	public String home() {
		return "fragments/docHome";
	}

    @GetMapping("/addDoctor")
    public String showDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor()); // Correct way to add a new object
        return "fragments/addDoctor"; // Returns the form page
    }

    @PostMapping("/addDoctor")
    public String addDoctor(@ModelAttribute("doctor") Doctor doctor, 
                            BindingResult result, 
                            Model model, 
                            RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<Doctor> response = restTemplate.postForEntity(BASE_URL + "/addDoctor", doctor, Doctor.class);
            model.addAttribute("message", "Doctor added successfully: " + response.getBody().getDoctorName().getFirstName());
            return "fragments/addDoctorForm"; // Redirect to avoid form resubmission
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", e.getMessage()); 
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add doctor, please try again.");
        }

        return "fragments/addDoctor"; // Stay on the form page to show errors
    }
    
  
	  @GetMapping("/updateDoctorForm")
	  public String enterDoctorIdForm() {
	      return "fragments/updateDoctor"; // Renders enterDoctorId.html
	  }
	
	  // Step 2: Fetch Doctor details and show update form
	  @GetMapping("/updateDoctor")
	  public String updateDoctorForm(@RequestParam("doctorId") int doctorId, Model model) {
	      try {
	          ResponseEntity<Doctor> response = restTemplate.getForEntity(BASE_URL + "/viewDoctorById/" + doctorId, Doctor.class);
	          model.addAttribute("doctor", response.getBody());
	          return "fragments/updateDoctor"; // Renders updateDoctor.html
	      } catch (Exception e) {
	          model.addAttribute("error", "Doctor not found with ID: " + doctorId);
	          return "fragments/updateDoctor"; // Redirects back to enterDoctorId.html with an error
	      }
	  }
	
	  // Step 3: Update Doctor details
	  @PutMapping("/updateDoctor")
	  public String updateDoctor(@RequestParam("doctorId") int doctorId, @ModelAttribute Doctor doctor, Model model) {
	      try {
	          restTemplate.put(BASE_URL + "/updateDoctor/" + doctorId, doctor);
	          model.addAttribute("message", "Doctor updated successfully: " + doctor.getDoctorName().getFirstName());
	      } catch (Exception e) {
	          model.addAttribute("error", "Failed to update doctor. Please try again.");
	      }
	      return "fragments/updateDoctor"; // Renders updateMessage.html with success/error message
	  }
    
    @GetMapping("/viewDoctorById")
    public String showDoctorForm() {
        return "fragments/viewDoctorById"; // Ensure this matches your Thymeleaf template
    }

    @GetMapping("/viewDoctorById/{doctorId}")
    public String viewDoctorById(@PathVariable Integer doctorId, Model model) {
        try {
           ResponseEntity<Doctor> response = restTemplate.getForEntity(
                BASE_URL + "/viewDoctorById/" + doctorId, Doctor.class);
            model.addAttribute("doctor", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Doctor not found with ID: " + doctorId);
        }

        return "fragments/viewDoctorById"; // Redirect to avoid form resubmission
    }
    
    @GetMapping("/viewDoctorBySpecialization")
    public String viewBySpecializationForm() {
        return "fragments/viewBySpec"; // Ensure this matches your Thymeleaf template
    }

    @GetMapping("/viewDoctorBySpecialization/{specialization}")
    public String viewDoctorBySpecialization(@PathVariable String specialization, Model model) {
        try {
           ResponseEntity<Doctor> response = restTemplate.getForEntity(
                BASE_URL + "/viewDoctorBySpecialization/" + specialization, Doctor.class);
            model.addAttribute("doctor", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Doctor not found with Specialization: " + specialization);
        }

        return "fragments/viewBySpec"; // Redirect to avoid form resubmission
    }
    
    
    @GetMapping("/viewDoctorBySlotAvailability")
    public String viewBySlotAvailabilityForm() {
        return "fragments/slotAvailability"; // Ensure this matches your Thymeleaf template
    }

    @GetMapping("/viewDoctorBySlotAvailability/{slot}")
    public String viewDoctorBySlotAvailability(@PathVariable LocalDateTime slot , Model model) {
        try {
           ResponseEntity<Doctor> response = restTemplate.getForEntity(
                BASE_URL + "/viewDoctorBySlotAvailability/" + slot, Doctor.class);
            model.addAttribute("doctor", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Doctors not found for the given slot");
        }

        return "fragments/slotAvailability"; // Redirect to avoid form resubmission
    }

    
     @GetMapping("/viewDoctorByDayAvailability")
	 public String viewDoctorAvailabilityByDayForm() {
	     return "fragments/dayAvailability";
	 }

	 @GetMapping("/viewDoctorByDayAvailability/{day}")
	 public String viewDoctorByDayAvailability(
	         @PathVariable("availableSlots") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day, 
	         Model model) {
	     try {
	         ResponseEntity<Doctor[]> response = restTemplate.getForEntity(
	             BASE_URL + "/viewDoctorAvailabilityByDay/" + day, Doctor[].class);

	         Doctor[] doctors = response.getBody();
	         if (doctors == null || doctors.length == 0) {
	             model.addAttribute("error", "No doctors available for the given day");
	         } else {
	             model.addAttribute("doctors", doctors);
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	         model.addAttribute("error", "An error occurred while fetching doctor availability.");
	     }
	     return "fragments/dayAvailability";
	 }
	 
	 @GetMapping("/viewByConsultationCountForm")
	 public String viewByConsultationCountForm() {
	     return "fragments/viewByConsultationCount"; // Thymeleaf template for the form
	 }

	 // Handle form submission and display results
	 @GetMapping("/viewByConsultationCount")
	 public String viewDoctorsByConsultationCount(
	         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	         @RequestParam int minCount,
	         Model model) {
	     try {
	         // Use URI variables to prevent direct string concatenation
	         String url = BASE_URL + "/viewDoctorsByConsultationCount?startDate={startDate}&endDate={endDate}&minCount={minCount}";

	         ResponseEntity<Doctor[]> response = restTemplate.getForEntity(url, Doctor[].class, startDate, endDate, minCount);

	         Doctor[] doctors = response.getBody();
	         if (doctors == null || doctors.length == 0) {
	             model.addAttribute("error", "No doctors found for the given criteria.");
	         } else {
	             model.addAttribute("doctors", doctors);
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	         model.addAttribute("error", "An error occurred while fetching doctor consultation count.");
	     }

	     return "fragments/viewByConsultationCount"; // Thymeleaf template for displaying results
	 }
    
      @GetMapping("/viewAllDoctors")
	  public String viewAllDoctors(Model model) {
	      ResponseEntity<List> response = restTemplate.getForEntity(
	          BASE_URL + "/viewAllDoctors", List.class);
	
	      model.addAttribute("doctors", response.getBody());
	      return "fragments/viewAllDoctors"; // Renders viewAllDoctors.html
	  }
		
	  @GetMapping("/viewAllActiveDoctors")
	  public String viewAllActiveDoctors(Model model) {
	      ResponseEntity<List> response = restTemplate.getForEntity(
	          BASE_URL + "/viewAllActiveDoctors", List.class);
	
	      model.addAttribute("doctors", response.getBody());
	      return "fragments/viewAllActiveDoctors"; // Renders viewAllDoctors.html
	  }
	  
	    @GetMapping("/deactivateDoctorForm")
	    public String deactivateDoctorForm() {
	        return "fragments/deactivateDoctor"; // Ensure this matches your Thymeleaf template
	    }

	    @GetMapping("/deactivateDoctor/{doctorId}")
	    public String deactivateDoctor(@PathVariable Integer doctorId, Model model) {
	        try {
	        	restTemplate.exchange(BASE_URL + "/deactivateDoctor/" + doctorId, HttpMethod.PUT, null, Void.class);
	            model.addAttribute("message", "Doctor deactivated with ID:"+doctorId);
	        } catch (Exception e) {
	            model.addAttribute("error", "Doctor not found to be deactivated: " + doctorId);
	        }

	        return "fragments/deactivateDoctor"; // Redirect to avoid form resubmission
	    }
	  
	  @GetMapping("/doctorRevenueReportForm")
		 public String getDoctorRevenueReportForm() {
		     return "fragments/doctorRevenueReport"; // Thymeleaf template for the form
		 }
	
		 // Handle form submission and display revenue report
		 @GetMapping("/doctorRevenueReport")
		 public String getDoctorRevenueReport(
		         @RequestParam("doctorId") int doctorId,
		         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		         Model model) {
		     try {
		         // Use URI parameters instead of directly appending them in the path
		         String url = BASE_URL + "/getDoctorRevenueReport/{doctorId}?startDate={startDate}&endDate={endDate}";
	
		         ResponseEntity<Double> response = restTemplate.getForEntity(url, Double.class, doctorId, startDate, endDate);
	
		         Double revenue = response.getBody();
		         if (revenue == null || revenue== 0.0) {
		             model.addAttribute("error", "Revenue report is not available for the given criteria.");
		         } else {
		             model.addAttribute("revenue", revenue);
		         }
		     } catch (Exception e) {
		         e.printStackTrace();
		         model.addAttribute("error", "An error occurred while fetching the revenue report.");
		     }
	       
		     return "fragments/doctorRevenueReport"; // Thymeleaf template for displaying results
		 }
	  
	  
	  
	  @GetMapping("/lowConsultationReportForm")
		 public String getLowConsultationDoctorsReportForm() {
		     return "fragments/lowConsultationReport"; // Thymeleaf template for the form
		 }
	
		 // Handle form submission and display low consultation doctors report
		 @GetMapping("/LowConsultationReport")
		 public String getLowConsultationDoctorsReport(
		         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		         Model model) {
		     try {
		         // Use query parameters instead of direct path variables
		         String url = BASE_URL + "/getLowConsultationDoctorsReport?startDate={startDate}&endDate={endDate}";
	
		         ResponseEntity<Doctor[]> response = restTemplate.getForEntity(url, Doctor[].class, startDate, endDate);
	
		         Doctor[] doctors = response.getBody();
		         if (doctors == null || doctors.length == 0) {
		             model.addAttribute("error", "No doctors found with low consultations in the given period.");
		         } else {
		             model.addAttribute("doctors", doctors);
		         }
		     } catch (Exception e) {
		         e.printStackTrace();
		         model.addAttribute("error", "An error occurred while fetching the report.");
		     }
	
		     return "fragments/lowConsultationReport"; // Thymeleaf template for displaying results
		 }
		 
		 @GetMapping("/frequentlyBookedDoctorsReportForm")
		 public String FrequentlyBookedDoctorsReportForm() {
		     return "fragments/frequentlyBookedReport"; // Thymeleaf template for the form
		 }
	
		 // Handle form submission and display low consultation doctors report
		 @GetMapping("/frequentlyBookedDoctorsReport")
		 public String getMostFrequentlyBookedDoctorsReport(
		         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		         Model model) {
		     try {
		         // Use query parameters instead of direct path variables
		    	 String url = BASE_URL + "/getMostFrequentlyBookedDoctorsReport?startDate=" + startDate + "&endDate=" + endDate;
		         ResponseEntity<Doctor[]> response = restTemplate.getForEntity(url, Doctor[].class, startDate, endDate);
	
		         Doctor[] doctors = response.getBody();
		         if (doctors == null || doctors.length == 0) {
		             model.addAttribute("error", "No most frequently booked doctors found.");
		         } else {
		             model.addAttribute("doctors", doctors);
		         }
		         model.addAttribute("doctors", doctors);
		     } catch (Exception e) {
		         e.printStackTrace();
		         model.addAttribute("error", "An error occurred while fetching the report.");
		     }
	
		     return "fragments/frequentlyBookedReport"; // Thymeleaf template for displaying results
		 }
		 
		 @GetMapping("/doctorAvailabilityReportForm")
		 public String getDoctorAvailabilityReportForm() {
		     return "fragments/doctorAvailabilityReport"; // Thymeleaf template for the form
		 }
	
		 // Handle form submission and display doctor availability report
		 @GetMapping("/doctorAvailabilityReport")
		 public String getDoctorAvailabilityReport(
		         @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
		         Model model) {
		     try {
		         // Use query parameters instead of path variables
		         String url = BASE_URL + "/getDoctorAvailabilityReport?date={date}";
	
		         ResponseEntity<Doctor[]> response = restTemplate.getForEntity(url, Doctor[].class, date);
	
		         Doctor[] doctors = response.getBody();
		         if (doctors == null || doctors.length == 0) {
		             model.addAttribute("error", "No doctors are available on " + date);
		         } else {
		             model.addAttribute("doctors", doctors);
		         }
		     } catch (Exception e) {
		         e.printStackTrace();
		         model.addAttribute("error", "An error occurred while fetching the doctor availability report.");
		     }
	
		     return "fragments/doctorAvailabilityReport"; // Thymeleaf template for displaying results
		 }
		 
		 @GetMapping("/doctorAppointmentReportForm")
		 public String getDoctorAppointmentReportForm() {
		     return "fragments/doctorAppointmentReport"; // Thymeleaf template for the form
		 }
	
		 // Handle form submission and display revenue report
		 @GetMapping("/getDoctorAppointmentReport")
		 public String getDoctorAppointmentReport(
		         @RequestParam("doctorId") int doctorId,
		         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		         Model model) {
		     try {
		         // Use URI parameters instead of directly appending them in the path
		         String url = BASE_URL + "/getDoctorAppointmentReport/{doctorId}?startDate={startDate}&endDate={endDate}";
	
		         ResponseEntity<Appointment[]> response = restTemplate.getForEntity(url, Appointment[].class, doctorId, startDate, endDate);
		         Appointment[] appointments = response.getBody();
		         if (appointments == null || appointments.length==0) {
		             model.addAttribute("error", "Appointments are not available");
		         } else {
		             model.addAttribute("appointments", appointments);
		         }
		     } catch (Exception e) {
		         e.printStackTrace();
		         model.addAttribute("error", "An error occurred while fetching the appointment report.");
		     }
	       
		     return "fragments/doctorAppointmentReport"; // Thymeleaf template for displaying results
		 }
		 
		 
		 
		 
		 // Patients
		 
		 
		 
		 
		 
		 @GetMapping("/viewDoctorBySpecializationFormPD")
			public String viewDoctorBySpecializationFormPD() {
				return "viewDoctorBySpecializationFormPD";
			}
			
		 @GetMapping("/viewDoctorBySpecializationPD")
			public String viewDoctorBySpecializationPD(@RequestParam("specialization") String specialization, Model model) {
			    try {
			        ResponseEntity<List> response = restTemplate.getForEntity(
			            BASE_URL + "/viewDoctorBySpecialization/" + specialization, List.class);

			        List doctors = response.getBody();

			        if (doctors == null || doctors.isEmpty()) {
			            model.addAttribute("error", "No doctors found for specialization: " + specialization);
			        } else {
			            model.addAttribute("doctors", doctors);
			        }
			    } catch (Exception e) {
			        e.printStackTrace();
			        model.addAttribute("error", "No doctors found: " + specialization);
			    }
			    return "viewDoctorBySpecializationPD";
			}
		 
		 
		 @GetMapping("/viewDoctorAvailabilityBySlotFormPD")
		    public String viewDoctorAvailabilityBySlotFormPD() {
		        return "viewDoctorAvailabilityBySlotFormPD";
		    }
		 
		 @GetMapping("/viewDoctorAvailabilityBySlotPD")
		 public String viewDoctorAvailabilityBySlotPD(
		         @RequestParam("availableSlots") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime availableSlots, 
		         Model model) {
		     try {
		         ResponseEntity<Doctor[]> response = restTemplate.getForEntity(
		             BASE_URL + "/viewDoctorAvailabilityBySlot/" + availableSlots, Doctor[].class);

		         Doctor[] doctors = response.getBody();
		         if (doctors == null || doctors.length == 0) {
		             model.addAttribute("error", "No doctors available for the given slot: " + availableSlots);
		         } else {
		             model.addAttribute("doctors", doctors);
		         }
		     } catch (Exception e) {
		         e.printStackTrace();
		         model.addAttribute("error", "An error occurred while fetching doctor availability.");
		     }
		     return "viewDoctorAvailabilityBySlotPD";
		 }
		 
		 
		 @GetMapping("/viewAllActiveDoctorsPD")
		    public String viewAllActiveDoctorsPD(Model model) {
		        ResponseEntity<List> response = restTemplate.getForEntity(
		            BASE_URL + "/viewAllActiveDoctors", List.class);

		        model.addAttribute("doctors", response.getBody());
		        return "viewAllActiveDoctorsPD"; // Renders viewAllDoctors.html
		    }
		 
		 
		 @GetMapping("/viewDoctorAvailabilityByDayFormPD")
		 public String viewDoctorAvailabilityByDayFormPD() {
		     return "viewDoctorAvailabilityByDayFormPD";
		 }
		 
		 @GetMapping("/viewDoctorAvailabilityByDayPD")
		 public String viewDoctorAvailabilityByDayPD(
		         @RequestParam("availableSlots") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate availableSlots, 
		         Model model) {
		     try {
		         ResponseEntity<Doctor[]> response = restTemplate.getForEntity(
		             BASE_URL + "/viewDoctorAvailabilityByDay/" + availableSlots, Doctor[].class);

		         Doctor[] doctors = response.getBody();
		         if (doctors == null || doctors.length == 0) {
		             model.addAttribute("error", "No doctors available for the given day: " + availableSlots);
		         } else {
		             model.addAttribute("doctors", doctors);
		         }
		     } catch (Exception e) {
		         e.printStackTrace();
		         model.addAttribute("error", "An error occurred while fetching doctor availability.");
		     }
		     return "viewDoctorAvailabilityByDayPD";
		 }
		 
		 @GetMapping("/viewDoctorsByConsultationCountFormPD")
		 public String viewDoctorsByConsultationCountFormPD() {
		     return "viewDoctorsByConsultationCountFormPD"; // Thymeleaf template for the form
		 }
		 
		 @GetMapping("/viewDoctorsByConsultationCountPD")
		 public String viewDoctorsByConsultationCountPD(
		         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		         @RequestParam("minCount") int minCount,
		         Model model) {
		     try {
		         // Use URI variables to prevent direct string concatenation
		         String url = BASE_URL + "/viewDoctorsByConsultationCount?startDate={startDate}&endDate={endDate}&minCount={minCount}";

		         ResponseEntity<Doctor[]> response = restTemplate.getForEntity(url, Doctor[].class, startDate, endDate, minCount);

		         Doctor[] doctors = response.getBody();
		         if (doctors == null || doctors.length == 0) {
		             model.addAttribute("error", "No doctors found for the given criteria.");
		         } else {
		             model.addAttribute("doctors", doctors);
		         }
		     } catch (Exception e) {
		         e.printStackTrace();
		         model.addAttribute("error", "An error occurred while fetching doctor consultation count.");
		     }

		     return "viewDoctorsByConsultationCountPD"; // Thymeleaf template for displaying results
		 }
		 
		 @GetMapping("/getDoctorAvailabilityReportFormPD")
		 public String getDoctorAvailabilityReportFormPD() {
		     return "getDoctorAvailabilityReportFormPD"; // Thymeleaf template for the form
		 }
		 
		 
		 @GetMapping("/getDoctorAvailabilityReportPD")
		 public String getDoctorAvailabilityReportPD(
		         @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
		         Model model) {
		     try {
		         // Use query parameters instead of path variables
		         String url = BASE_URL + "/getDoctorAvailabilityReport?date={date}";

		         ResponseEntity<Doctor[]> response = restTemplate.getForEntity(url, Doctor[].class, date);

		         Doctor[] doctors = response.getBody();
		         if (doctors == null || doctors.length == 0) {
		             model.addAttribute("error", "No doctors are available on " + date);
		         } else {
		             model.addAttribute("doctors", doctors);
		         }
		     } catch (Exception e) {
		         e.printStackTrace();
		         model.addAttribute("error", "An error occurred while fetching the doctor availability report.");
		     }

		     return "getDoctorAvailabilityReportPD"; // Thymeleaf template for displaying results
		 }

		 
		 
}
