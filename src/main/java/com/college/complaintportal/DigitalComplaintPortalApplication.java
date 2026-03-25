package com.college.complaintportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DigitalComplaintPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalComplaintPortalApplication.class, args);
    }
}
