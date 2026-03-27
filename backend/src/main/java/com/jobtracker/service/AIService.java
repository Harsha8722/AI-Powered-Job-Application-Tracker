package com.jobtracker.service;

import com.jobtracker.model.Resume;
import com.jobtracker.model.User;
import com.jobtracker.repository.ResumeRepository;
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
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Autowired
    private AuthService authService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ResumeRepository resumeRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> analyzeResume(String email, Long resumeId) {
        User user = authService.getUserByEmail(email);
        Resume resume;
        if (resumeId != null) {
            resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new RuntimeException("Resume not found with id: " + resumeId));
        } else {
            resume = resumeService.getLatestResume(user.getId());
        }

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

            Map<String, Object> result = new HashMap<>();
            if (response.getBody() != null) {
                result.putAll(response.getBody());
                // Normalize field names: Python returns snake_case, Java expects camelCase
                normalizeKeys(result);
                resumeService.updateAnalysisResult(resume.getId(), result.toString());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("AI service unavailable: " + e.getMessage());
        }
    }

    public Map<String, Object> matchResume(String jobDescription, String email, Long resumeId) {
        User user = authService.getUserByEmail(email);
        Resume resume;
        if (resumeId != null) {
            resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new RuntimeException("Resume not found with id: " + resumeId));
        } else {
            resume = resumeService.getLatestResume(user.getId());
        }

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

            Map<String, Object> result = new HashMap<>();
            if (response.getBody() != null) {
                result.putAll(response.getBody());
                // Normalize field names for frontend
                normalizeKeys(result);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("AI service unavailable: " + e.getMessage());
        }
    }

    /**
     * Converts Python snake_case keys to JavaScript camelCase keys.
     * E.g.,: match_score -> matchScore, matching_skills -> matchingSkills
     */
    private void normalizeKeys(Map<String, Object> map) {
        // match_score -> matchScore
        if (map.containsKey("match_score")) {
            Object val = map.remove("match_score");
            map.put("matchScore", val);
        }
        // match_percentage -> matchPercentage
        if (map.containsKey("match_percentage")) {
            Object val = map.remove("match_percentage");
            map.put("matchPercentage", val);
        }
        // detected_skills -> detectedSkills (also add as 'skills' alias)
        if (map.containsKey("detected_skills")) {
            Object val = map.remove("detected_skills");
            map.put("detectedSkills", val);
            map.put("skills", val);
        }
        // required_skills -> requiredSkills
        if (map.containsKey("required_skills")) {
            Object val = map.remove("required_skills");
            map.put("requiredSkills", val);
        }
        // matching_skills -> matchingSkills
        if (map.containsKey("matching_skills")) {
            Object val = map.remove("matching_skills");
            map.put("matchingSkills", val);
        }
        // missing_skills -> missingSkills
        if (map.containsKey("missing_skills")) {
            Object val = map.remove("missing_skills");
            map.put("missingSkills", val);
        }
        // extra_skills -> extraSkills
        if (map.containsKey("extra_skills")) {
            Object val = map.remove("extra_skills");
            map.put("extraSkills", val);
        }
        // skills_count -> skillsCount
        if (map.containsKey("skills_count")) {
            Object val = map.remove("skills_count");
            map.put("skillsCount", val);
        }
        // estimated_experience_years -> estimatedExperienceYears
        if (map.containsKey("estimated_experience_years")) {
            Object val = map.remove("estimated_experience_years");
            map.put("estimatedExperienceYears", val);
        }
        // word_count -> wordCount
        if (map.containsKey("word_count")) {
            Object val = map.remove("word_count");
            map.put("wordCount", val);
        }
    }
}
