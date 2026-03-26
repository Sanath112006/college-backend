package com.college.complaintportal.service;

import com.college.complaintportal.entity.Complaint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Transactional(readOnly = true)
    public void sendStatusUpdateEmail(Complaint complaint) {
        if (complaint.getIsAnonymous() || complaint.getUser() == null || complaint.getUser().getEmail() == null) {
            log.info("Skipping status update email for anonymous or missing user email. Complaint ID: {}", complaint.getId());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(complaint.getUser().getEmail());
            message.setSubject("Complaint Status Updated - Digital Complaint Portal");
            
            String body = String.format(
                    "Hello %s,\n\n" +
                    "The status of your complaint has been updated by an administrator.\n\n" +
                    "Complaint ID: %d\n" +
                    "Title: %s\n" +
                    "New Status: %s\n\n" +
                    "You can log in to the Digital Complaint Portal to view more details.\n\n" +
                    "Best regards,\nDigital Complaint Portal Team",
                    complaint.getUser().getName(),
                    complaint.getId(),
                    complaint.getTitle(),
                    complaint.getStatus().name()
            );
            
            message.setText(body);
            mailSender.send(message);
            log.info("Status update email sent successfully to {} for complaint ID: {}", complaint.getUser().getEmail(), complaint.getId());
        } catch (Exception e) {
            log.error("Failed to send status update email to {} for complaint ID: {}. Error: {}",
                    complaint.getUser().getEmail(), complaint.getId(), e.getMessage());
            // We catch the exception so it doesn't interrupt the calling transaction
        }
    }

    @Async
    @Transactional(readOnly = true)
    public void sendResponseEmail(Complaint complaint, String adminMessage) {
        if (complaint.getIsAnonymous() || complaint.getUser() == null || complaint.getUser().getEmail() == null) {
            log.info("Skipping response email for anonymous or missing user email. Complaint ID: {}", complaint.getId());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(complaint.getUser().getEmail());
            message.setSubject("New Admin Response - Digital Complaint Portal");
            
            String body = String.format(
                    "Hello %s,\n\n" +
                    "An administrator has left a new response on your complaint.\n\n" +
                    "Complaint ID: %d\n" +
                    "Title: %s\n\n" +
                    "Admin Response:\n%s\n\n" +
                    "You can log in to the Digital Complaint Portal to view the full details.\n\n" +
                    "Best regards,\nDigital Complaint Portal Team",
                    complaint.getUser().getName(),
                    complaint.getId(),
                    complaint.getTitle(),
                    adminMessage
            );
            
            message.setText(body);
            mailSender.send(message);
            log.info("Admin response email sent successfully to {} for complaint ID: {}", complaint.getUser().getEmail(), complaint.getId());
        } catch (Exception e) {
            log.error("Failed to send admin response email to {} for complaint ID: {}. Error: {}",
                    complaint.getUser().getEmail(), complaint.getId(), e.getMessage());
        }
    }
}
