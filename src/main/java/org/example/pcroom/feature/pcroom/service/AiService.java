package org.example.pcroom.feature.pcroom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String AI_SERVER_URL = "http://localhost:8000/analyze";

    public String analyzeBlueprint(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() != null ? file.getOriginalFilename() : "blueprint.jpg";
            }
        };
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(AI_SERVER_URL, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to connect to AI server", e);
            throw new RuntimeException("AI 서버 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
