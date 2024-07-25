// ActivityLogService.java
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
}
