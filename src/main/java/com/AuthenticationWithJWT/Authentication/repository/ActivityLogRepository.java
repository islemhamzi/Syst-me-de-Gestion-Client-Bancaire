package com.AuthenticationWithJWT.Authentication.repository;

import com.AuthenticationWithJWT.Authentication.entities.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
