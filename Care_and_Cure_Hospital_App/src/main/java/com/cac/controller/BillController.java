package com.cac.controller;
	
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cac.dto.BillDTO;
import com.cac.dto.BillMapper;
import com.cac.entity.Appointment;
import com.cac.entity.Bill;
import com.cac.service.BillService;
	
	
	@CrossOrigin(origins = "http://localhost:8081") 
	@RestController
	@RequestMapping("/bills")
	public class BillController {
	
		
			private final BillService billService;
		    private final BillMapper billMapper;
		    
		    @Autowired
		    public BillController(BillService billService, BillMapper billMapper) {
		        this.billService = billService;
		        this.billMapper = billMapper;
		    }
		    
		    @GetMapping("/{billId}")
		    public ResponseEntity<BillDTO> viewBillById(@PathVariable int billId) {
		        Bill bill = billService.viewBillById(billId);
		        if (bill == null) {
		            return ResponseEntity.notFound().build();
		        }
		        return ResponseEntity.ok(billMapper.toDto(bill));
		    }
		    
	    
		    @GetMapping("/getAllBills")  
		    public ResponseEntity<List<BillDTO>> getAllBills() {
		        List<Bill> bills = billService.viewAllBills();
		        return ResponseEntity.ok(billMapper.toDtoList(bills));
		    }

		    @GetMapping("/patient/{patientId}")
		    public ResponseEntity<List<BillDTO>> viewBillsByPatient(@PathVariable int patientId) {
		        List<Bill> bills = billService.viewBillsByPatient(patientId);
		        return ResponseEntity.ok(billMapper.toDtoList(bills));
		    }

		    @GetMapping("/date/{billDate}")
		    public ResponseEntity<List<BillDTO>> viewBillsByDate(@PathVariable String billDate) {
		        List<Bill> bills = billService.viewBillsByDate(LocalDate.parse(billDate));
		        return ResponseEntity.ok(billMapper.toDtoList(bills));
		    }

		    @GetMapping("/unpaid")
		    public ResponseEntity<List<BillDTO>> viewUnpaidBills() {
		        List<Bill> unpaidBills = billService.viewUnpaidBills();
		        return ResponseEntity.ok(billMapper.toDtoList(unpaidBills));
		    }

	    
	    @GetMapping("/appointments/patient/{patientId}")
	    public ResponseEntity<List<Appointment>> getAppointmentsByPatientId(@PathVariable String patientId) {
	        List<Appointment> appointments = billService.getAppointmentsByPatientId(patientId);
	        
	        // Debugging: Print fetched appointments
	        if (appointments.isEmpty()) {
	            System.out.println("No appointments found for patientId: " + patientId);
	        } else {
	            for (Appointment appointment : appointments) {
	                System.out.println("Fetched Appointment -> ID: " + appointment.getAppointmentId() + ", Date: " + appointment.getAppointmentDate());
	            }
	        }
	        
	        return ResponseEntity.ok(appointments);
	    }


	
	  
	    @GetMapping("/discounted")
	    public ResponseEntity<List<Bill>> viewBillsWithDiscount() {
	        List<Bill> discountedBills = billService.viewBillsWithDiscount();
	        return ResponseEntity.ok(discountedBills);
	    }
	    
	    
	   
	    
	    @PostMapping("/generateBill")
	    @ResponseBody
	    public ResponseEntity<String> generateBillAndSendEmail(@RequestParam int billId) {
	        try {
	            String pdfPath = billService.generateBillPdf(billId);
	            return ResponseEntity.ok("Bill generated, saved, and emailed successfully! PDF Path: " + pdfPath);
	        } catch (Exception e) {
	            return ResponseEntity.internalServerError().body("Failed to generate bill: " + e.getMessage());
	        }
	    }
	
	    @GetMapping("/download")
	    public ResponseEntity<Resource> downloadFile(@RequestParam String file) throws IOException {
	        return billService.downloadBill(file);
	    }
	    
	    
	    @PutMapping("/update")
	    public ResponseEntity<?> updateBill(@RequestBody Bill updatedBill) {
	        if (updatedBill == null || updatedBill.getBillId() == 0) {
	            return ResponseEntity.badRequest().body("⚠ Error: Bill ID is required for update!");
	        }

	        try {
	            Bill bill = billService.updateBill(updatedBill);
	            return ResponseEntity.ok(bill);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error updating bill: " + e.getMessage());
	        }
	    }

	    
	    @PostMapping("/add")
	    public ResponseEntity<Bill> addBill(@RequestParam int appointmentId, @RequestBody Bill newBill) {
	        Bill savedBill = billService.addBill(appointmentId, newBill);
	        return ResponseEntity.status(HttpStatus.CREATED).body(savedBill);
	    }
	    
	}