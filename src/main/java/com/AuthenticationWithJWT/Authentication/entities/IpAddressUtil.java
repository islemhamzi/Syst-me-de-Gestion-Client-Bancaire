package com.AuthenticationWithJWT.Authentication.entities;

import jakarta.servlet.http.HttpServletRequest;

public class IpAddressUtil {
    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-Forwarded-For");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.getRemoteAddr();
            } else {
                // In case of multiple forwarded IPs, take the first one
                remoteAddr = remoteAddr.split(",")[0].trim();
            }

            // Normalize IPv6 localhost address to IPv4
            if ("0:0:0:0:0:0:0:1".equals(remoteAddr)) {
                remoteAddr = "127.0.0.1";
            }
        }

        return remoteAddr;
    }
}
