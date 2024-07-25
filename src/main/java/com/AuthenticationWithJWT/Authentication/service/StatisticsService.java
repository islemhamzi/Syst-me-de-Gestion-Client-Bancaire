package com.AuthenticationWithJWT.Authentication.service;

import com.AuthenticationWithJWT.Authentication.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final DocumentRepository documentRepository;

    public Map<String, Long> getDocumentsByType() {
        List<Object[]> results = documentRepository.countDocumentsByType();
        return convertToMap(results);
    }

    public Map<String, Long> getDocumentsConsultedByType() {
        List<Object[]> results = documentRepository.countDocumentsConsultedByType();
        return convertToMap(results);
    }

    public Map<String, Long> getDocumentsEmailedByType() {
        List<Object[]> results = documentRepository.countDocumentsEmailedByType();
        return convertToMap(results);
    }

    public Map<String, Map<String, Long>> getDocumentsByTypeAndAgency() {
        List<Object[]> results = documentRepository.countDocumentsByTypeAndAgency();
        Map<String, Map<String, Long>> resultMap = new HashMap<>();
        for (Object[] result : results) {
            String type = (String) result[0];
            String agency = (String) result[1];
            Long count = (Long) result[2];

            resultMap.putIfAbsent(type, new HashMap<>());
            resultMap.get(type).put(agency, count);
        }
        return resultMap;
    }

    private Map<String, Long> convertToMap(List<Object[]> results) {
        Map<String, Long> resultMap = new HashMap<>();
        for (Object[] result : results) {
            resultMap.put((String) result[0], (Long) result[1]);
        }
        return resultMap;
    }
}