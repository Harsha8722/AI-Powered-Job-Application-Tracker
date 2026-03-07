package com.jobtracker.service;

import com.jobtracker.model.Resume;
import com.jobtracker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Autowired
    private AuthService authService;

    @Autowired
    private ResumeService resumeService;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> analyzeResume(String email) {
        User user = authService.getUserByEmail(email);
        Resume resume = resumeService.getLatestResume(user.getId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(new File(resume.getFilePath())));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServiceUrl + "/analyze",
                    requestEntity,
                    Map.class
            );

            Map<String, Object> result = response.getBody();
            if (result != null) {
                resumeService.updateAnalysisResult(resume.getId(), result.toString());
            }
            return result != null ? result : new HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException("AI service unavailable: " + e.getMessage());
        }
    }

    public Map<String, Object> matchResume(String jobDescription, String email) {
        User user = authService.getUserByEmail(email);
        Resume resume = resumeService.getLatestResume(user.getId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(new File(resume.getFilePath())));
            body.add("job_description", jobDescription);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServiceUrl + "/match",
                    requestEntity,
                    Map.class
            );

            return response.getBody() != null ? response.getBody() : new HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException("AI service unavailable: " + e.getMessage());
        }
    }
}
