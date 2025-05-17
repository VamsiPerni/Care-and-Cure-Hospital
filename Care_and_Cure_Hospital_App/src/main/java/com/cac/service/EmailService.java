package com.cac.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cac.entity.Appointment;
import com.cac.entity.Bill;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service
public class EmailService { 
    @Autowired
    private JavaMailSender mailSender;

     public void sendDoctorRegistrationEmail(String toEmail, String doctorName) {
        String subject = "Doctor Registration Successful";
        String body = "Dear " + doctorName + ",<br><br>"
                    + "Welcome to Care and Cure Hospital!<br>"
                    + "Your registration is successful. You can now log in and start managing appointments.<br><br>"
                    + "Best Regards,<br>Care and Cure Hospital Team";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true indicates HTML content
            mailSender.send(message);
            System.out.println("Email sent successfully to " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email to " + toEmail);
        }
    }
     
     public void sendDoctorUpdationEmail(String toEmail, String doctorName) {
         String subject = "Profile Updated";
         String body = "Dear " + doctorName + ",<br><br>"
                     + "Your Profile has been updated successfully.<br><br>"
                     + "Regards,<br>Care and Cure Hospital Team";

         try {
             MimeMessage message = mailSender.createMimeMessage();
             MimeMessageHelper helper = new MimeMessageHelper(message, true);
             
             helper.setTo(toEmail);
             helper.setSubject(subject);
             helper.setText(body, true); // true indicates HTML content
             mailSender.send(message);
             System.out.println("Email sent successfully to " + toEmail);
         } catch (MessagingException e) {
             e.printStackTrace();
             System.out.println("Failed to send email to " + toEmail);
         }
     }

    public void sendAppointmentConfirmation(String toEmail, Appointment appointment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            
            helper.setTo(toEmail);
            helper.setSubject("Appointment Confirmation - " + appointment.getAppointmentId());
            helper.setText("<p>Dear Patient,</p>"
                + "<p>Your appointment has been successfully booked.</p>"
                + "<p>Appointment Details:</p>"
                + "<ul>"
                + "<li><b>Date:</b> " + appointment.getAppointmentDate() + "</li>"
                + "<li><b>Time:</b> " + appointment.getSlot() + "</li>"
                + "</ul>"
                + "<p>Regards,<br>Hospital Management</p>", true);

            mailSender.send(message);
            System.out.println("Appointment confirmation email sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send appointment confirmation email", e);
        }
    }
    
    //========================bill email service====================
    
    public void sendBillReceiptWithPdf(String toEmail, Bill bill, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Hospital Bill Receipt - Bill ID: " + bill.getBillId());
            helper.setText("Dear Patient,<br><br>"
                    + "Please find attached your bill receipt.<br><br>"
                    + "Regards,<br>Hospital Management", true);

            helper.addAttachment("Bill_" + bill.getBillId() + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email with PDF attachment", e);
        }
    }
    public void sendReportWithAttachmentBill(String toEmail, String reportName, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Transaction Report: " + reportName);
            helper.setText("Dear Admin,<br><br>"
                    + "Please find attached the requested report: <b>" + reportName + "</b>.<br><br>"
                    + "Regards,<br>Hospital Management System", true);

            helper.addAttachment(reportName + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            System.out.println("Email with report sent successfully to " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email with report attachment", e);
        }
    }
    //========================bill email service====================

    
    //============payment email service==============================
    public void sendReportWithAttachment(String toEmail, String reportName, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Transaction Report: " + reportName);
            helper.setText("Dear Admin,<br><br>"
                    + "Please find attached the requested report: <b>" + reportName + "</b>.<br><br>"
                    + "Regards,<br>Hospital Management System", true);

            helper.addAttachment(reportName + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            System.out.println("Email with report sent successfully to " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email with report attachment", e);
        }
    }
    

}