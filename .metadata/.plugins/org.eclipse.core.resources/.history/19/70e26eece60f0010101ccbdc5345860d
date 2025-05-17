package com.cac.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cac.entity.Appointment;
import com.cac.entity.Bill;
import com.cac.exception.BillNotFound;
import com.cac.repository.AppointmentRepository;
import com.cac.repository.BillRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

@Service
public class BillService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

 
        public String generateBillPdf(int billId) {
            Bill bill = billRepository.findById(billId)
                    .orElseThrow(() -> new BillNotFound("Bill not found with ID: " + billId));

            try {
                // Define PDF save location
                String pdfPath = "C:/generated_bills/Bill_" + bill.getBillId() + ".pdf";
                File pdfFile = new File(pdfPath);
                pdfFile.getParentFile().mkdirs(); // Ensure directory exists

                // Create PDF document
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();

                // HEADER: Hospital Logo, Name, and Address
                PdfPTable headerTable = new PdfPTable(2);
                headerTable.setWidthPercentage(100);
                headerTable.setWidths(new int[]{1, 3});

                Image logo = getLogoImage();
                if (logo != null) {
                    logo.scaleToFit(70, 70);
                    PdfPCell logoCell = new PdfPCell(logo);
                    logoCell.setBorder(Rectangle.NO_BORDER);
                    logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    headerTable.addCell(logoCell);
                }

             // Hospital Name & Address Formatting
                Font hospitalNameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20); // Bold font for Hospital Name
                Font addressFont = FontFactory.getFont(FontFactory.HELVETICA, 12); // Regular font for Address
                Font phoneFont = FontFactory.getFont(FontFactory.HELVETICA, 10); // Smaller font for Phone number

                // Create formatted text
                Chunk hospitalName = new Chunk("Care and Cure Hospital\n", hospitalNameFont);
                Chunk address = new Chunk("Sneh Nagar, Bhopal\nMadhya Pradesh 462022\n", addressFont);
                Chunk phone = new Chunk("1234567890", phoneFont);

                // Combine into a paragraph
                Paragraph hospitalDetails = new Paragraph();
                hospitalDetails.add(hospitalName);
                hospitalDetails.add(address);
                hospitalDetails.add(phone);
                hospitalDetails.setAlignment(Element.ALIGN_CENTER); // ✅ Center align text

                


                PdfPCell hospitalCell = new PdfPCell();
                hospitalCell.addElement(hospitalDetails);
                hospitalCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(hospitalCell);

                document.add(headerTable);
                document.add(new Paragraph("\n"));
                
                document.add(new LineSeparator());

                // BODY: Patient & Bill Information
//                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
//                document.add(new Paragraph("Hospital Bill Receipt", titleFont));
                document.add(new Paragraph("\n"));

                document.add(new Paragraph("Patient Name: " + bill.getAppObj().getPatient().getPatientName()));
                document.add(new Paragraph("Patient ID: " + bill.getAppObj().getPatient().getPatientId()));
                document.add(new Paragraph("Bill ID: " + bill.getBillId()));
                document.add(new Paragraph("Bill Date: " + bill.getBillDate()));
                document.add(new Paragraph("\n"));

                // BILL DETAILS TABLE
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setWidths(new float[]{3, 2, 1, 2});

             // Table Header
                String[] headers = {"Description", "Cost", "Qty", "Amount"};
                for (String header : headers) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(headerCell);
                }

                // Fetch medication cost
                double medicationCost = bill.getMedicineFees();
                double finalMedicationCost = medicationCost; // Default: No discount

                // ✅ Apply 10% discount if medication cost is greater than ₹1000
                if (medicationCost > 1000) {
                    finalMedicationCost = medicationCost - (medicationCost * 0.10);
                }

                // Add Bill Items
                addTableRow(table, "Consultation", bill.getConsultationFees(), 1,bill.getConsultationFees());
                addTableRow(table, "Medication", medicationCost, 1, finalMedicationCost); 
                addTableRow(table, "Miscellaneous", bill.getMiscellaneousCharge(), 1,bill.getMiscellaneousCharge());
                addTableRow(table, "Tests", bill.getTestCharges(), 1,bill.getTestCharges());

                // Calculate Total After Discount
                double newTotal = bill.getTotalAmount();
                if (medicationCost > 1000) {
                    newTotal -= (medicationCost * 0.10); // Subtract discount from total
                }

                // Total Row
                PdfPCell totalLabel = new PdfPCell(new Phrase("Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                totalLabel.setColspan(2);
                table.addCell(totalLabel);
                table.addCell(""); // Empty cell
                table.addCell("₹" + newTotal); // Updated total with discount


                document.add(table);
                document.add(new Paragraph("\n"));
                document.add(new LineSeparator());
                document.add(new Paragraph("\n"));

                // FOOTER: Thank You Message
                Paragraph footer = new Paragraph("Thank you for choosing Care and Cure Hospital!", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
                footer.setAlignment(Element.ALIGN_CENTER);
                document.add(footer);

                document.close();

                // Send the email with the saved PDF\ bill.getAppObj().getPatient().getEmailId();
                String patientEmail ="nafisnafis391@gmail.com";
                emailService.sendBillReceiptWithPdf(patientEmail, bill, Files.readAllBytes(pdfFile.toPath()));

                return pdfPath;

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error generating PDF: " + e.getMessage());
            }
        }

        private Image getLogoImage() {
            try (InputStream is = getClass().getResourceAsStream("/static/icon/carecureicon.png")) {
                if (is == null) {
                    throw new RuntimeException("Logo image not found in resources!");
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                buffer.flush();

                return Image.getInstance(buffer.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error loading logo image!", e);
            }
        }


     // Helper method to add rows to the table
        private void addTableRow(PdfPTable table, String description, double cost, int qty, double amount) {
            table.addCell(new PdfPCell(new Phrase(description)));
            table.addCell(new PdfPCell(new Phrase("₹" + cost)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(qty))));
            table.addCell(new PdfPCell(new Phrase("₹" + String.format("%.2f", (amount * qty))))); // Format amount to 2 decimal places
        }

    

    
 

    // 🔹 Download Bill PDF
    public ResponseEntity<Resource> downloadBill(String filePath) throws IOException {
        File pdfFile = new File(filePath);
        if (!pdfFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Path path = pdfFile.toPath();
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFile.getName())
                .body(resource);
    }

    // 🔹 Add New Bill
    public Bill addBill(int appointmentId, Bill newBill) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Optional<Bill> existingBill = billRepository.findByAppObj(appointment);
        if (existingBill.isPresent()) {
            throw new RuntimeException("A bill already exists for this appointment!");
        }

        newBill.setAppObj(appointment);
        newBill.setBillDate(LocalDate.now());
        return billRepository.save(newBill);
    }

    // 🔹 View Bill By ID
    public Bill viewBillById(int billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new BillNotFound("Bill not found with ID: " + billId));
    }

    // 🔹 View Bills by Patient ID
    public List<Bill> viewBillsByPatient(int patientId) {
        return billRepository.findBillsByPatientId(patientId);
    }

    // 🔹 View Bills by Date
    public List<Bill> viewBillsByDate(LocalDate date) {
        return billRepository.findByBillDate(date);
    }

    // 🔹 View Discounted Bills
    public List<Bill> viewBillsWithDiscount() {
        return billRepository.findBillsWithDiscount();
    }

    // 🔹 View Unpaid Bills
    public List<Bill> viewUnpaidBills() {
        return billRepository.findUnpaidBills();
    }

    // 🔹 Update Existing Bill
    public Bill updateBill(Bill updatedBill) {
        Bill existingBill = billRepository.findById(updatedBill.getBillId())
                .orElseThrow(() -> new BillNotFound("Bill not found with ID: " + updatedBill.getBillId()));

        existingBill.setConsultationFees(updatedBill.getConsultationFees());
        existingBill.setMedicineFees(updatedBill.getMedicineFees());
        existingBill.setTestCharges(updatedBill.getTestCharges());
        existingBill.setMiscellaneousCharge(updatedBill.getMiscellaneousCharge());
        existingBill.setDescription(updatedBill.getDescription());
        existingBill.setInsuranceApplicable(updatedBill.isInsuranceApplicable());
        existingBill.setTax(updatedBill.isTax());

        // Recalculate discount and total amount
        float discount = (updatedBill.getMedicineFees() > 1000) ? 10.0f : 0.0f;
        existingBill.setDiscountPercentage(discount);

        double total = updatedBill.getConsultationFees() + updatedBill.getMedicineFees()
                + updatedBill.getTestCharges() + updatedBill.getMiscellaneousCharge();
        total -= (total * discount / 100);

        existingBill.setTotalAmount(total);

        return billRepository.save(existingBill);
    }

    // 🔹 View All Bills
    public List<Bill> viewAllBills() {
        return billRepository.findAll();
    }
    
    
    
    public List<Appointment> getAppointmentsByPatientId(String patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments;
    }
    
   

}