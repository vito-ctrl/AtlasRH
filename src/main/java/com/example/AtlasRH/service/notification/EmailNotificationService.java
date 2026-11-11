package com.example.AtlasRH.service.notification;

import com.example.AtlasRH.entity.LeaveRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    public void sendLeaveDecisionEmail(LeaveRequest request) {
        String recipient = request.getEmployee().getEmail();
        String employeeName = request.getEmployee().getPrenom() + " " + request.getEmployee().getNom();
        String status = request.getStatus().name();
        String leaveType = request.getLeaveType().getName();

        log.info("==================== SIMULATION EMAIL SMTP ====================");
        log.info("À : {}", recipient);
        log.info("Objet : Décision concernant votre demande de congé [{}]", status);
        log.info("Bonjour {},", employeeName);
        log.info("Votre demande de {} du {} au {} ({} jours) a été {}.",
                leaveType, request.getDateDebut(), request.getDateFin(), request.getDaysCount(), status);
        if (request.getManagerComment() != null && !request.getManagerComment().isBlank()) {
            log.info("Commentaire du responsable : \"{}\"", request.getManagerComment());
        }
        log.info("Cordialement, L'équipe AtlasRH");
        log.info("===============================================================");
    }
}
