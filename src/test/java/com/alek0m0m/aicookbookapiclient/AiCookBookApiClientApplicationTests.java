package com.alek0m0m.aicookbookapiclient;

import com.alek0m0m.aicookbookapiclient.dto.ChatResponse;
import com.alek0m0m.aicookbookapiclient.dto.Choice;
import com.alek0m0m.aicookbookapiclient.dto.Message;
import com.alek0m0m.aicookbookapiclient.dto.Usage;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)  // Starts the web server with a random port
class AiCookBookApiClientApplicationTests {

    @Autowired
    private WebTestClient webTestClient; // WebTestClient for performing requests

    @MockBean
    private WebClient.Builder webClientBuilder;  // Mock WebClient.Builder to mock the API calls

    private static MockWebServer mockWebServer;

    @Value("${OPENAI_APIKEY}")
    private String openapiKey;

    @BeforeAll
    static void setUp() throws IOException {
        // Create and start a MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        // Shut down the MockWebServer after all tests
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        // Initialize WebClient to point to the MockWebServer
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())  // Set the base URL of MockWebServer
                .build();
        // Set the mock WebClient in the application context
        webClientBuilder.build().equals(webClient);
    }

    @Test
    void getChat_ShouldReturnChatResponse() throws Exception {
        // Create a mock response object
        ChatResponse mockResponse = new ChatResponse();

        // Create mock Choice and Usage objects
        Choice choice = new Choice();
        choice.setMessage(new Message("user","mock message"));  // Set text in Choice object
        mockResponse.setChoices(List.of(choice));  // Add choice to the response

        Usage usage = new Usage();
        usage.setPromptTokens(10);
        usage.setCompletionTokens(10);
        usage.setTotalTokens(20);
        mockResponse.setUsage(usage);  // Set usage in the response

        // Convert the mock response to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String mockResponseBody = objectMapper.writeValueAsString(mockResponse);

        // Enqueue a mock response to the MockWebServer
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseBody)  // Set the body of the mock response
                .addHeader("Content-Type", "application/json"));  // Set the Content-Type header

        // Perform the API call and verify the response
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/chat").queryParam("message", "Hello").build())
                .exchange()
                .expectStatus().isOk()  // Check if the status is OK
                .expectBody(Map.class)  // Expect a Map in the response body
                .consumeWith(response -> {
                    Map<String, Object> responseBody = response.getResponseBody();
                    assertNotNull(responseBody);  // Assert the response is not null

                    // Extract choices from the response body and assert the response structure
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                    assertEquals(1, choices.size());  // Assert the number of choices
                    assertEquals("mocked response", choices.get(0).get("text"));  // Assert the text of the choice

                    // Extract usage from the response body and assert totalTokens
                    Map<String, Object> usageMap = (Map<String, Object>) responseBody.get("usage");
                    assertEquals(20, usageMap.get("totalTokens"));  // Assert totalTokens is 20
                });
    }
}
