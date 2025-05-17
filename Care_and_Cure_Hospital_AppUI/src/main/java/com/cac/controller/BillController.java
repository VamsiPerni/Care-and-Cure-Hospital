package com.cac.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cac.dto.BillDTO;
import com.cac.entity.Appointment;
import com.cac.entity.Bill;

@Controller
@RequestMapping("/billing")
  public class BillController {

    @Autowired
    private RestTemplate restTemplate;

    private final String BILL_SERVICE_URL = "http://localhost:8080/bills";
    private final String APPOINTMENT_SERVICE_URL = "http://localhost:8080/bills";

    
    @GetMapping("/search")
    public String searchPatient(@RequestParam int patientId) {
        return "redirect:/billing/" + patientId;
    }

    @GetMapping("/{patientId}")
    public String getPatientBills(@PathVariable String patientId, Model model) {
        try {
            ResponseEntity<Bill[]> response = restTemplate.getForEntity(
                BILL_SERVICE_URL + "/patient/" + patientId, Bill[].class
            );
            ResponseEntity<Appointment[]> appointmentResponse = restTemplate.getForEntity(
                APPOINTMENT_SERVICE_URL + "/appointments/patient/" + patientId, Appointment[].class
            );

            Bill[] bills = response.getBody();
            Appointment[] appointments = appointmentResponse.getBody();

            // Debugging: Print fetched appointments
            if (appointments != null) {
                for (Appointment appointment : appointments) {
                    System.out.println("Appointment ID: " + appointment.getAppointmentId() + ", Date: " + appointment.getAppointmentDate());
                }
            } else {
                System.out.println("No appointments found for patientId: " + patientId);
            }

            model.addAttribute("bills", bills);
            model.addAttribute("appointments", appointments);
            model.addAttribute("patientId", patientId);
        } catch (Exception e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }

        return "add-bill";
    }

    

    @GetMapping("/addNewBill")
    public String addNewBill(){
    	return "add-bill";
    }
    @GetMapping("/updateBill")
    public String editBill() {
        return "update-bill";
    }

    
    @GetMapping("/edit")
    public String editBill(@RequestParam int billId, @RequestParam int patientId, Model model) {
        try {
            // Fetch bill by billId
            ResponseEntity<Bill> billResponse = restTemplate.getForEntity(
                BILL_SERVICE_URL + "/" + billId, Bill.class
            );

            Bill bill = billResponse.getBody();
            if (bill == null) {
                model.addAttribute("error", "Bill not found!");
                return "update-bill"; // Stay on the same page with an error message
            }

            // Fetch all bills by patientId
            ResponseEntity<Bill[]> billsResponse = restTemplate.getForEntity(
                BILL_SERVICE_URL + "/patient/" + patientId, Bill[].class
            );

            List<Bill> bills = billsResponse.getBody() != null ? Arrays.asList(billsResponse.getBody()) : new ArrayList<>();

            // Generate PDF for the bill
            restTemplate.postForEntity(
                BILL_SERVICE_URL + "/generateBill?billId=" + billId, null, String.class
            );

            model.addAttribute("bill", bill);
            model.addAttribute("bills", bills);
            model.addAttribute("patientId", patientId);

        } catch (Exception e) {
            System.out.println("Error fetching bill data: " + e.getMessage());
            model.addAttribute("error", "Failed to load bill details. Please try again.");
        }

        return "update-bill"; // Stays on the update-bill page even if an error occurs
    }


    @GetMapping("/billHome")
    public String manageBill(Model model) {
        try {
            // Fetch bills from an external API
            ResponseEntity<Bill[]> response = restTemplate.getForEntity(BILL_SERVICE_URL + "/bills", Bill[].class);

            // Convert response to a list and pass it to the view
            List<Bill> bills = response.getBody() != null ? List.of(response.getBody()) : new ArrayList<>();

            model.addAttribute("bills", bills);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching bill data.");
        }

        return "bill-home"; // Ensure this matches your Thymeleaf template name
    }


    @PostMapping("/add")
    public String addBill(@RequestParam int patientId,
                          @RequestParam int appointmentId,
                          @RequestParam double consultationFees,
                          @RequestParam double medicineFees,
                          @RequestParam double testCharges,
                          @RequestParam double miscellaneousCharge,
                          @RequestParam String description,
                          @RequestParam boolean isInsuranceApplicable,
                          @RequestParam boolean tax) {

        Bill newBill = new Bill();
        newBill.setConsultationFees(consultationFees);
        newBill.setMedicineFees(medicineFees);
        newBill.setTestCharges(testCharges);
        newBill.setMiscellaneousCharge(miscellaneousCharge);
        newBill.setDescription(description);
        newBill.setInsuranceApplicable(isInsuranceApplicable);
        newBill.setTax(tax);

        float discountPercentage = (medicineFees > 1000) ? 10.0f : 0.0f;
        newBill.setDiscountPercentage(discountPercentage);

        double totalAmount = consultationFees + medicineFees + testCharges + miscellaneousCharge;
        if (discountPercentage > 0) {
            totalAmount -= (totalAmount * discountPercentage / 100);
        }
        if (tax) {
            totalAmount += (totalAmount * 0.18);
        }
        if (isInsuranceApplicable) {
            totalAmount -= (totalAmount * 0.20);
        }

        newBill.setTotalAmount(totalAmount);

        // 🛠 Debugging Logs
        System.out.println("Adding new bill: " + newBill);
        
        try {
        	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BILL_SERVICE_URL + "/add")
        		    .queryParam("appointmentId", appointmentId);

        		ResponseEntity<Bill> response = restTemplate.postForEntity(
        		    builder.toUriString(), newBill, Bill.class
        		);

            System.out.println("📩 API Response: " + response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                int billId = response.getBody().getBillId();
                System.out.println("✅ Bill Created: " + billId);

                try {
                    UriComponentsBuilder bu = UriComponentsBuilder.fromHttpUrl(BILL_SERVICE_URL + "/generateBill")
                            .queryParam("billId", billId);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

                    ResponseEntity<String> pdfResponse = restTemplate.exchange(
                            bu.toUriString(), HttpMethod.POST, requestEntity, String.class
                    );

                    if (pdfResponse.getStatusCode() == HttpStatus.OK) {
                        System.out.println("📄 PDF Generated Successfully: " + pdfResponse.getBody());
                    } else {
                        System.err.println("⚠ Failed to Generate PDF: " + pdfResponse.getStatusCode());
                    }

                } catch (Exception e) {
                    System.err.println("❌ Exception in PDF Generation: " + e.getMessage());
                }

            } else {
                System.err.println("❌ Error: Bill not created!");
            }

        } catch (Exception e) {
            System.err.println("⚠ Exception: " + e.getMessage());
        }

        return "redirect:/billing/" + patientId;
    }


    @GetMapping("/update/search")
    public String searchBillsForUpdate(@RequestParam int patientId, Model model) {
        ResponseEntity<Bill[]> response = restTemplate.getForEntity(BILL_SERVICE_URL + "/patient/" + patientId, Bill[].class);
        model.addAttribute("bills", response.getBody());
        model.addAttribute("patientId", patientId);
        return "update-bill";
    }

    @PostMapping("/update")
    public String updateBill(@ModelAttribute Bill bill, @RequestParam int patientId) {
        if (bill.getBillId() == 0) {
            System.err.println("❌ Error: Bill ID is missing!");
            return "redirect:/billing/update/search?patientId=" + patientId;
        }
        System.out.println("Received Bill ID: " + bill.getBillId()); // Debugging

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Bill> requestEntity = new HttpEntity<>(bill, headers);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BILL_SERVICE_URL + "/update")
                    .queryParam("patientId", patientId);

            ResponseEntity<Bill> response = restTemplate.exchange(
                    builder.toUriString(), HttpMethod.PUT, requestEntity, Bill.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("✅ Bill Updated Successfully! " + response.getBody());
            } else {
                System.err.println("⚠ Failed to Update Bill! Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception while updating bill: " + e.getMessage());
        }

        return "redirect:/billing/update/search?patientId=" + patientId;
    }



        
    @GetMapping("/viewTransaction")
    public String homePage(
            @RequestParam(name = "filterType", required = false, defaultValue = "all") String filterType,
            @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery,
            @RequestParam(name = "sortedByDiscount", required = false, defaultValue = "false") boolean sortedByDiscount,
            @RequestParam(name = "sortBy", required = false, defaultValue = "none") String sortBy,
            @RequestParam(name = "onlyUnpaid", required = false, defaultValue = "false") boolean onlyUnpaid,
            Model model) {

        // Print input parameters
        System.out.println("=== Input Parameters ===");
        System.out.println("filterType: " + filterType);
        System.out.println("searchQuery: " + searchQuery);
        System.out.println("sortedByDiscount: " + sortedByDiscount);
        System.out.println("sortBy: " + sortBy);
        System.out.println("onlyUnpaid: " + onlyUnpaid);

        List<BillDTO> bills = new ArrayList<>();
        try {
            ResponseEntity<BillDTO[]> response = null;
            ResponseEntity<BillDTO> singleBillResponse = null;
            
            switch (filterType) {
                case "billId":
                    try {
                        System.out.println("Fetching bill by ID: " + searchQuery);
                        singleBillResponse = restTemplate.getForEntity(
                            BILL_SERVICE_URL + "/" + Integer.parseInt(searchQuery), 
                            BillDTO.class
                        );
                        if (singleBillResponse.getBody() != null) {
                            bills = List.of(singleBillResponse.getBody());
                            System.out.println("Found bill: " + singleBillResponse.getBody());
                        } else {
                            System.out.println("No bill found with ID: " + searchQuery);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Bill ID format: " + searchQuery);
                        model.addAttribute("error", "Invalid Bill ID format.");
                    }
                    break;
                    
                case "patientId":
                    try {
                        System.out.println("Fetching bills for patient ID: " + searchQuery);
                        response = restTemplate.getForEntity(
                            BILL_SERVICE_URL + "/patient/" + Integer.parseInt(searchQuery), 
                            BillDTO[].class
                        );
                        if (response.getBody() != null) {
                            bills = List.of(response.getBody());
                            System.out.println("Found " + response.getBody().length + " bills for patient");
                        } else {
                            System.out.println("No bills found for patient ID: " + searchQuery);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Patient ID format: " + searchQuery);
                        model.addAttribute("error", "Invalid Patient ID format.");
                    }
                    break;
                    
                case "date":
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate date = LocalDate.parse(searchQuery, formatter);
                        System.out.println("Fetching bills for date: " + date);
                        response = restTemplate.getForEntity(
                            BILL_SERVICE_URL + "/date/" + date, 
                            BillDTO[].class
                        );
                        if (response.getBody() != null) {
                            bills = List.of(response.getBody());
                            System.out.println("Found " + response.getBody().length + " bills for date");
                        } else {
                            System.out.println("No bills found for date: " + date);
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid date format: " + searchQuery);
                        model.addAttribute("error", "Invalid date format. Please use dd/MM/yyyy.");
                    }
                    break;
                    
                default:
                    System.out.println("Fetching all bills");
                    response = restTemplate.getForEntity(
                        BILL_SERVICE_URL + "/getAllBills", 
                        BillDTO[].class
                    );
                    if (response.getBody() != null) {
                        bills = List.of(response.getBody());
                        System.out.println("Found " + response.getBody().length + " total bills");
                    } else {
                        System.out.println("No bills found");
                    }
                    break;
            }
            
            // Print bills before unpaid filter
            System.out.println("=== Bills before unpaid filter ===");
            bills.forEach(bill -> System.out.println(bill.toString()));
            
            // Handle unpaid bills filter
            if (onlyUnpaid) {
                try {
                    System.out.println("Applying unpaid filter");
                    ResponseEntity<BillDTO[]> unpaidResponse = restTemplate.getForEntity(
                        BILL_SERVICE_URL + "/unpaid", 
                        BillDTO[].class
                    );
                    
                    if (unpaidResponse.getBody() != null) {
                        int unpaidCount = unpaidResponse.getBody().length;
                        System.out.println("Found " + unpaidCount + " unpaid bills");
                        
                        if ("patientId".equals(filterType)) {
                            int patientId = Integer.parseInt(searchQuery);
                            System.out.println("Filtering unpaid bills for patient ID: " + patientId);
                            bills = Arrays.stream(unpaidResponse.getBody())
                                    .filter(bill -> bill.getAppObj() != null &&
                                                    bill.getAppObj().getPatientObj() != null &&
                                                    bill.getAppObj().getPatientObj().getPatientId() == patientId)
                                    .collect(Collectors.toList());
                            System.out.println("Found " + bills.size() + " unpaid bills for this patient");
                        } else {
                            bills = List.of(unpaidResponse.getBody());
                        }
                        
                        model.addAttribute("unpaidCount", unpaidCount);
                    } else {
                        System.out.println("No unpaid bills found");
                    }
                } catch (Exception ex) {
                    System.out.println("Error fetching unpaid bills: " + ex.getMessage());
                    model.addAttribute("error", "Error fetching unpaid bills.");
                }
            }
            
            // Print bills after unpaid filter
            System.out.println("=== Bills after unpaid filter ===");
            bills.forEach(bill -> System.out.println(bill.toString()));
            
            // Apply additional filters and sorting
            if (sortedByDiscount) {
                System.out.println("Applying discount filter");
                bills = bills.stream()
                        .filter(b -> b.getDiscountPercentage() > 0)
                        .sorted(Comparator.comparingDouble(BillDTO::getDiscountPercentage).reversed())
                        .collect(Collectors.toList());
                System.out.println("Found " + bills.size() + " bills with discount");
            }
            
            System.out.println("Applying sorting: " + sortBy);
            bills = new ArrayList<>(bills); // Make sure the list is mutable
            switch (sortBy) {
                case "date":
                    bills.sort(Comparator.comparing(BillDTO::getBillDate));
                    break;
                case "amount":
                    bills.sort(Comparator.comparingDouble(BillDTO::getTotalAmount));
                    break;
                default:
                    System.out.println("No sorting applied");
                    break;
            }
            
            // Print final bill list
            System.out.println("=== Final bill list ===");
            bills.forEach(bill -> {
                System.out.println("Bill ID: " + bill.getBillId());
                System.out.println("  Amount: " + bill.getTotalAmount());
                System.out.println("  Date: " + bill.getBillDate());
                System.out.println("  Discount: " + bill.getDiscountPercentage());
                if (bill.getAppObj() != null) {
                    System.out.println("  Appointment ID: " + bill.getAppObj().getAppointmentId());
                    if (bill.getAppObj().getPatientObj() != null) {
                        System.out.println("  Patient: " + bill.getAppObj().getPatientObj().getPatientName() + 
                                         " (ID: " + bill.getAppObj().getPatientObj().getPatientId() + ")");
                    }
                }
                System.out.println("-------------------");
            });
            
            // Calculate statistics
            int totalInvoices = bills.size();
            int discountedCount = (int) bills.stream()
                                          .filter(b -> b.getDiscountPercentage() > 0)
                                          .count();
            
            System.out.println("=== Statistics ===");
            System.out.println("Total invoices: " + totalInvoices);
            System.out.println("Discounted invoices: " + discountedCount);
            System.out.println("Unpaid count: " + model.getAttribute("unpaidCount"));
            
            // Add attributes to model
            model.addAttribute("bills", bills);
            model.addAttribute("totalInvoices", totalInvoices);
            model.addAttribute("discountedCount", discountedCount);
            model.addAttribute("filterType", filterType);
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("sortedByDiscount", sortedByDiscount);
            model.addAttribute("onlyUnpaid", onlyUnpaid);
            model.addAttribute("sortBy", sortBy);
            
        } catch (Exception e) {
            System.out.println("Error in homePage: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error fetching data from backend: " + e.getMessage());
        }
        
        return "view-transaction";
    }
}