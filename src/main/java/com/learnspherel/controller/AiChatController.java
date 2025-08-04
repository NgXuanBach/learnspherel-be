package com.learnspherel.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnspherel.dto.AiChatRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-chat")
public class AiChatController {
    private final String OLLAMA_API_URL = "http://localhost:11434/api/chat";

    @PostMapping
    public ResponseEntity<?> chatWithOllama(@RequestBody AiChatRequest aiRequest) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // FIX: Đảm bảo StringHttpMessageConverter dùng UTF-8
            List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
            for (HttpMessageConverter<?> converter : messageConverters) {
                if (converter instanceof StringHttpMessageConverter) {
                    ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
                }
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiRequest.getModel() == null ? "llama3" : aiRequest.getModel());
            requestBody.put("messages", aiRequest.getMessages());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String ollamaResp = restTemplate.postForObject(OLLAMA_API_URL, entity, String.class);

            // Xử lý NDJSON trả về đúng tiếng Việt
            StringBuilder content = new StringBuilder();
            String[] lines = ollamaResp.split("\\r?\\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(line);
                    if (node.has("message") && node.get("message").has("content")) {
                        content.append(node.get("message").get("content").asText());
                    }
                } catch (Exception ex) { /* skip */ }
            }
            String aiContent = content.toString();
            if (aiContent.isBlank()) aiContent = "Xin lỗi, hiện tại tôi không thể trả lời.";
            return ResponseEntity.ok(Collections.singletonMap("answer", aiContent));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("answer", "Lỗi gọi Ollama: " + e.getMessage()));
        }
    }
}

