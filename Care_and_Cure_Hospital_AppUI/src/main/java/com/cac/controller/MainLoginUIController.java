package com.cac.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainLoginUIController {

    @GetMapping("/mainLoginPage")
    public String login(Model model) {
        model.addAttribute("error", ""); // Initialize error message
        return "mainLoginPage"; // Displays the login page
    }

    @PostMapping("/mainLoginPage")
    public String handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "Username and Password are required.");
            return "mainLoginPage";
        }

        // Admin Login (e.g., admin1, admin2)
        if (username.matches("admin\\d+") && password.equals("admin123")) {
            System.out.println("Admin login successful: " + username);
            return "redirect:/adminDashboard";
        }
        
        // Doctor Login (ID starts with 125XXXXX)
        if (username.matches("125\\d+") && password.equals("doctorPass")) {
            System.out.println("Doctor login successful: " + username);
            String doctorIdStr = username.substring(3);
            int doctorId = Integer.parseInt(doctorIdStr);
            System.out.println("Doctor login successful: " + doctorId);
            return "redirect:/dashboard?doctorId=" + doctorId;
        }

        // Patient Login (Email-based)
        if (username.contains("@") && username.contains(".") && password.equals("patientPass")) {
            System.out.println("Patient login successful: " + username);
            return "redirect:/ui/patients/patientDashboard";
        }

        // If no match, show error
        model.addAttribute("error", "Invalid username or password.");
        return "mainLoginPage";
    }
}
