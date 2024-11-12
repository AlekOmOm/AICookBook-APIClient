package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.config.OpenAIConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URI;

@Service
public class APIClientService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final OpenAIConfig openAIConfig;

    public APIClientService(OpenAIConfig openAIConfig) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.openAIConfig = openAIConfig;
    }

    public String sendPromt(String prompt) throws IOException, InterruptedException {
        if (openAIConfig.getKey() == null || openAIConfig.getKey().isEmpty()) {
            throw new IllegalStateException("OpenAI key is not set");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openAIConfig.getEndpoint())).header("Authorization", "Bearer " + openAIConfig.getKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseResponse(response.body());
        }else {
            throw new IOException("Error from OpenAI API: " + response.statusCode() + " - " + response.body());

        }
    }

    private String parseResponse(String responseBody) throws IOException {
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        List<Map<String, Object>> choises = (List<Map<String, Object>>) responseMap.get("choises");
        if (!choises.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choises.get(0).get("message");
            return (String) message.get("content");
        }
        throw new IOException("unexpected response format");
    }
}
