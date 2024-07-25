package com.AuthenticationWithJWT.Authentication.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        logger.info("Twilio initialized with account SID {}", accountSid);
    }

    public void sendSms(String toPhoneNumber, String message) {
        try {
            if (!toPhoneNumber.startsWith("+")) {
                toPhoneNumber = "+216" + toPhoneNumber; // Adjust the country code as needed
            }
            logger.info("Sending SMS to {}", toPhoneNumber);
            Message.creator(new com.twilio.type.PhoneNumber(toPhoneNumber), new com.twilio.type.PhoneNumber(fromPhoneNumber), message).create();
            logger.info("SMS sent successfully to {}", toPhoneNumber);
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}", toPhoneNumber, e);
        }
    }
}
