package com.AuthenticationWithJWT.Authentication.service;

import com.AuthenticationWithJWT.Authentication.entities.ActivityLog;
import com.AuthenticationWithJWT.Authentication.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    public List<ActivityLog> getAllLogs() {
        return activityLogRepository.findAll();
    }

    public void logActivity(String action, String username, String role, String ip) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setDate(Instant.now());
        log.setUsername(username);
        log.setRole(role);
        log.setIp(ip);
        activityLogRepository.save(log);
    }

    public void logDocumentsViewed(String username, String role, String ip) {
        logActivity(String.format("L'utilisateur %s a consulté ses documents", username), username, role, ip);
    }

    public void logDocumentDownloaded(String username, String role, String ip, String documentName) {
        logActivity(String.format("L'utilisateur %s a téléchargé le document %s", username, documentName), username, role, ip);
    }

    public void logEmailSent(String username, String role, String ip, String recipientEmail, String documentName) {
        logActivity(String.format("L'utilisateur %s a envoyé le document %s à %s", username, documentName, recipientEmail), username, role, ip);
    }

    public void logDocumentDeleted(String username, String role, String ip, String documentName) {
        logActivity(String.format("L'utilisateur %s a supprimé le document %s", username, documentName), username, role, ip);
    }

    public void logDelegationDeleted(String username, String role, String ip, Long delegationId) {
        logActivity(String.format("L'utilisateur %s a supprimé la délégation avec ID %d", username, delegationId), username, role, ip);
    }

    public void logDelegationCreated(String delegatorUsername, String delegateUsername, String role, String ip, String startDate, String endDate) {
        logActivity(String.format("L'utilisateur %s a délégué ses documents à %s du %s au %s", delegatorUsername, delegateUsername, startDate, endDate), delegatorUsername, role, ip);
    }
}
