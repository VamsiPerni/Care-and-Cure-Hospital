package com.cac.scheduler;

import com.cac.service.PatientService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PatientStatusScheduler {

    private final PatientService patientService;

    public PatientStatusScheduler(PatientService patientService) {
        this.patientService = patientService;
    }

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void runDeactivationTask() {
        // Deactivate patients who haven't visited in 2+ years
        patientService.deactivateInactivePatients();
    }
}
