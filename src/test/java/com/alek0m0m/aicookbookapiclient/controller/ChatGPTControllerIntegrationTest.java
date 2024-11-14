package com.alek0m0m.aicookbookapiclient.controller;

import com.alek0m0m.aicookbookapiclient.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebFluxTest(ChatGPTController.class)
@Import(ChatGPTController.class)
class ChatGPTControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private WebClient.Builder webClientBuilder;

    private static MockWebServer mockWebServer;

    @Value("${OPENAI_APIKEY}")
    private String openapiKey;
    @Qualifier("messageSource")
    @Autowired
    private MessageSource messageSource;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        webClientBuilder.build().equals(webClient);
    }

    @Test
    void getChat_ShouldReturnChatResponse() throws Exception {
        // Mock response data
        ChatResponse mockResponse = new ChatResponse();

        // Assuming Choice has a constructor that takes a message or text, or use a setter method
        Choice choice = new Choice();
        choice.setMessage(new Message("message","mocked message"));  // adjust this based on the actual field name in Choice
        mockResponse.setChoices(List.of(choice));

        // Assuming Usage has a no-args constructor and appropriate setters
        Usage usage = new Usage();
        usage.setPromptTokens(10);
        usage.setCompletionTokens(10);
        usage.setTotalTokens(20);
        mockResponse.setUsage(usage);

        // Convert the response object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String mockResponseBody = objectMapper.writeValueAsString(mockResponse);

        // Enqueue a mock response
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseBody)
                .addHeader("Content-Type", "application/json"));

        // Call the API and verify the response
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/chat").queryParam("message", "Hello").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, Object> responseBody = response.getResponseBody();
                    assertNotNull(responseBody);

                    // Adjust field access based on your JSON response structure
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                    assertEquals(1, choices.size());
                    assertEquals("mocked response", choices.get(0).get("text"));

                    Map<String, Object> usageMap = (Map<String, Object>) responseBody.get("usage");
                    assertEquals(20, usageMap.get("totalTokens"));
                });
    }
}
