package org.cataract.web.config;

import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.PatientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PatientCleanScheduler {

    private final PatientService patientService;

    @Value("${app.patient.retention.cron}")
    private String patientCleanupCronExpression;

    @Value("${app.patient.retention.days}")
    private int patientRetentionDays;

    public PatientCleanScheduler(PatientService patientService) {
        this.patientService = patientService;
    }

    @Scheduled(cron = "${app.patient.retention.cron}")
    public void cleanupExpiredTokens() {

        int deletedCount = patientService.deletePatientByPolicy(patientRetentionDays);
        log.info("Scheduler ran with cron '{}', deleted {} patients.", patientCleanupCronExpression, deletedCount);
    }
}