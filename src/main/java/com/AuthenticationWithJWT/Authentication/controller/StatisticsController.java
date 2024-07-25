package com.AuthenticationWithJWT.Authentication.controller;

import com.AuthenticationWithJWT.Authentication.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/documents-by-type")
    public ResponseEntity<Map<String, Long>> getDocumentsByType() {
        return ResponseEntity.ok(statisticsService.getDocumentsByType());
    }

    @GetMapping("/documents-consulted-by-type")
    public ResponseEntity<Map<String, Long>> getDocumentsConsultedByType() {
        return ResponseEntity.ok(statisticsService.getDocumentsConsultedByType());
    }

    @GetMapping("/documents-emailed-by-type")
    public ResponseEntity<Map<String, Long>> getDocumentsEmailedByType() {
        return ResponseEntity.ok(statisticsService.getDocumentsEmailedByType());
    }

    @GetMapping("/documents-by-type-and-agency")
    public ResponseEntity<Map<String, Map<String, Long>>> getDocumentsByTypeAndAgency() {
        return ResponseEntity.ok(statisticsService.getDocumentsByTypeAndAgency());
    }
}