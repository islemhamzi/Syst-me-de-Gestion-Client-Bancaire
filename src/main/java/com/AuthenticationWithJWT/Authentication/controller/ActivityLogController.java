package com.AuthenticationWithJWT.Authentication.controller;

import com.AuthenticationWithJWT.Authentication.entities.ActivityLog;
import com.AuthenticationWithJWT.Authentication.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {
    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getAllLogs() {
        return ResponseEntity.ok(activityLogService.getAllLogs());
    }

    @PostMapping
    public ResponseEntity<Void> logActivity(@RequestBody ActivityLog activityLog) {
        activityLogService.logActivity(activityLog.getAction(), activityLog.getUsername(), activityLog.getRole(), activityLog.getIp());
        return ResponseEntity.ok().build();
    }
}
