package com.cac.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;  // Correct Spring import
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cac.dto.PaymentMethodAnalysisDTO;
import com.cac.dto.PaymentSummaryDTO;
import com.cac.entity.Payment;
import com.cac.service.PaymentService;


@CrossOrigin(origins = "http://localhost:8081") // Allow frontend requests from port 7213
@RestController
public class PaymentController
{
	@Autowired
	PaymentService paymentService;
	
	@GetMapping("/checkMessage")
	public String checkMessage()
	{
		return paymentService.checkMessage();
	}
	
	@PostMapping("/addTransaction")
	public ResponseEntity<Payment>addTransaction(@RequestBody Payment transaction)
	{
		Payment transactionObj = paymentService.addTransaction(transaction);
		
		return new ResponseEntity<>(transactionObj,HttpStatus.CREATED);
	}
	
	
	@GetMapping("/viewAllTransactions")
	public List<Payment>viewAllTransactions()
	{
		return paymentService.viewAllTransactions();
	}


	@GetMapping("/viewTransactionById/{transactionId}")
	public ResponseEntity<?> viewTransactionById(@PathVariable int transactionId) {
	    try {
	        Payment transaction = paymentService.viewTransactionById(transactionId);

	        if (transaction == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
	        }
	        
	        return ResponseEntity.ok(transaction);
	    } catch (Exception e) {
	        e.printStackTrace(); // Log error in the console
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching transaction");
	    }
	}

		
	@GetMapping("/viewTransactionsByMode")
	public ResponseEntity<List<Payment>>viewTransactionsByMode(@RequestParam String paymentMethod)
	{
		List<Payment> transactions = paymentService.viewTransactionsByPaymentMethod(paymentMethod);
		
		return ResponseEntity.ok(transactions);
	}
	
	
	@PutMapping("/updateTransaction/{transactionId}")
	public ResponseEntity<?> updateTransaction(@PathVariable int transactionId, 
	                                          @RequestBody Payment updatedTransaction) {
	    System.out.println("🚀 Update Transaction API Hit!");
	    System.out.println("Received Transaction ID: " + transactionId);
	    System.out.println("Received Updated Data: " + updatedTransaction);

	    Payment existingTransaction = paymentService.viewTransactionById(transactionId);
	    if (existingTransaction == null) {
	        System.out.println("❌ Transaction not found");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
	    }

	    updatedTransaction.setTransactionId(transactionId);
	    Payment updated = paymentService.updateTransaction(transactionId, updatedTransaction);

	    System.out.println("✅ Updated Transaction: " + updated);
	    return ResponseEntity.ok(updated);
	}

	
	@DeleteMapping("/deleteTransaction/{transactionId}")
	public ResponseEntity<String> deleteTransaction(@PathVariable int transactionId)
	{
		String message = paymentService.deleteTransaction(transactionId);
		return ResponseEntity.ok(message);
	}
	
	
	// ViewTransactionsByStatus 
	
	@GetMapping("/viewTransactionsByStatus/{status}")
    public ResponseEntity<List<Payment>> viewTransactionsByStatus(@PathVariable String status) {
        List<Payment> transactions = paymentService.getTransactionsByStatus(status);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(transactions);
    }
	
	// viewTransactionsByDate 
	
	 @GetMapping("/viewTransactionsByDate/{date}")
	    public ResponseEntity<List<Payment>> viewTransactionsByDate(@PathVariable String date) {
	        List<Payment> transactions = paymentService.getTransactionsByDate(date);
	        
	        if (transactions.isEmpty()) {
	            return ResponseEntity.noContent().build();
	        }
	        
	        return ResponseEntity.ok(transactions);
	    }
	
	 // Generate Daily Payment Summary Report code starts here 
	 
	 @GetMapping("/dailyPaymentSummary")
	    public PaymentSummaryDTO getDailyPaymentSummary(
	            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
	        LocalDate localDate = (date != null) ? date : LocalDate.now();
	        return paymentService.generateDailyPaymentSummaryReport(localDate);
	    } 
	 
	 
	 @GetMapping("/generatePaymentMethodAnalysisReport")
	    public ResponseEntity<PaymentMethodAnalysisDTO> generatePaymentMethodAnalysisReport(
	            @RequestParam("startDate") String startDate, 
	            @RequestParam("endDate") String endDate) {
	        
	        try {
	            // Convert String to LocalDate
	            LocalDate start = LocalDate.parse(startDate);
	            LocalDate end = LocalDate.parse(endDate);
	            
	            // Call service method to get the transactions between the given dates and completed status
	            List<Payment> completedTransactions = paymentService.getCompletedTransactions(start, end);
	            
	            // Calculate the number of transactions for each payment method
	            long creditCardCount = completedTransactions.stream().filter(txn -> txn.getPaymentMethod().equals("Credit Card")).count();
	            long debitCardCount = completedTransactions.stream().filter(txn -> txn.getPaymentMethod().equals("Debit Card")).count();
	            long upiCount = completedTransactions.stream().filter(txn -> txn.getPaymentMethod().equals("UPI")).count();
	            long cashCount = completedTransactions.stream().filter(txn -> txn.getPaymentMethod().equals("Cash")).count();
	            
	            // Create DTO to send the data back to frontend
	            PaymentMethodAnalysisDTO analysisDTO = new PaymentMethodAnalysisDTO(
	                    creditCardCount, debitCardCount, upiCount, cashCount);
	            
	            return ResponseEntity.ok(analysisDTO);
	            
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body(null);
	        }
	    }
	 
	 
	 @GetMapping("/generatePaymentHistoryReport")
	 public ResponseEntity<List<Payment>> generatePaymentHistoryReport(
	         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
	     List<Payment> transactions = paymentService.getTransactionsBetweenDates(startDate, endDate);
	     return ResponseEntity.ok(transactions);
	 } 
	 
	 
	 private void drawTable(PDPageContentStream contentStream, float y, float margin, String[][] content, int startRow, int endRow) throws IOException {
		final int cols = content[0].length;
		final float rowHeight = 20f;
		final float tableWidth = PDRectangle.A4.getWidth() - (2 * margin);
		final float colWidth = tableWidth / (float) cols;
		final float cellMargin = 5f;
	
		// Draw the rows
		float nextY = y;
		for (int i = startRow; i <= endRow; i++) {
			contentStream.drawLine(margin, nextY, margin + tableWidth, nextY);
			nextY -= rowHeight;
		}
	
		// Draw the columns
		float nextX = margin;
		for (int i = 0; i <= cols; i++) {
			contentStream.drawLine(nextX, y, nextX, y - ((endRow - startRow + 1) * rowHeight));
			nextX += colWidth;
		}
	
		// Add content to the table
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12); // Bold font for headings
		float textY = y - 15;
		for (int i = startRow; i <= endRow; i++) {
			float textX = margin + cellMargin;
			for (int j = 0; j < content[i].length; j++) {
				if (i == 0) { // Bold headings for the first row
					contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
				} else { // Normal font for other rows
					contentStream.setFont(PDType1Font.HELVETICA, 12);
				}
				contentStream.beginText();
				contentStream.newLineAtOffset(textX, textY);
				contentStream.showText(content[i][j]);
				contentStream.endText();
				textX += colWidth;
			}
			textY -= rowHeight;
		}
	}
	 
	 
	@GetMapping("/downloadTransactionsPDF")
	public ResponseEntity<byte[]> downloadTransactionsPDF() throws IOException {
		// Fetch all transactions
		List<Payment> transactions = paymentService.viewAllTransactions();
	
		// Prepare table content
		String[][] tableContent = new String[transactions.size() + 1][4];
		tableContent[0] = new String[]{"Transaction ID", "Amount Paid", "Payment Method", "Payment Status"}; // Headings
	
		// Fill table rows with transaction data
		for (int i = 0; i < transactions.size(); i++) {
			Payment transaction = transactions.get(i);
			tableContent[i + 1] = new String[]{
				String.valueOf(transaction.getTransactionId()),
				String.valueOf(transaction.getAmountPaid()),
				transaction.getPaymentMethod(),
				transaction.getPaymentStatus()
			};
		}
	
		// Create a PDF document
		try (PDDocument document = new PDDocument()) {
			float margin = 50;
			float yStart = 700;
			float rowHeight = 20f;
			int rowsPerPage = (int) ((yStart - margin) / rowHeight); // Number of rows that fit on one page
	
			int startRow = 0;
			while (startRow < tableContent.length) {
				// Add a new page
				PDPage page = new PDPage(PDRectangle.A4);
				document.addPage(page);
	
				try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
					// Add a title
					contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
					contentStream.beginText();
					contentStream.newLineAtOffset(margin, 750);
					contentStream.showText("Transaction Report");
					contentStream.endText();
	
					// Calculate the number of rows to draw on this page
					int endRow = Math.min(startRow + rowsPerPage, tableContent.length - 1); // Ensure endRow is within bounds
	
					// Draw the table for the current page
					drawTable(contentStream, yStart, margin, tableContent, startRow, endRow);
	
					// Update the start row for the next page
					startRow = endRow + 1;
				}
			}
	
			// Convert PDF to byte array
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			document.save(byteArrayOutputStream);
			byte[] pdfBytes = byteArrayOutputStream.toByteArray();
	
			// Set response headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDisposition(ContentDisposition.builder("attachment").filename("transactions_report.pdf").build());
	
			return ResponseEntity.ok()
					.headers(headers)
					.body(pdfBytes);
		}
	}
	 
	
}







