package com.carelink.appointment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AppointmentStatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentStatusScheduler.class);

    @Autowired
    private AppointmentStatusUpdateService updateService;

    @Scheduled(cron = "0 0 1 * * ?", zone = "Europe/Dublin")
    public void runDailyUpdate() {
        logger.info("Running daily appointment status update task...");
        updateService.updateYesterdayAttendedToCompleted();
    }
}
