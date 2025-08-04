package com.learnspherel.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AiChatRequest {
    private String model;
    private List<Map<String, String>> messages; // [{role, content}, ...]
}
