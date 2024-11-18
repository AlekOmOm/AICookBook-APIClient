package com.alek0m0m.aicookbookapiclient.controller;

import io.github.cdimascio.dotenv.Dotenv;
import com.alek0m0m.aicookbookapiclient.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChatGPTController {
    // Constants for Settings:
        // model options: gpt-4o, gpt-4o-mini, gpt-4-turbo, gpt-4, and gpt-3.5-turbo
    private static final String GPT_MODEL_NAME = "gpt-3.5-turbo";
    private static final double TEMPERATURE = 0.0;
    private static final int MAX_TOKENS = 30;
    private static final double PRESENCE_PENALTY = 1;
    private static final String BASE_URL = "https://api.openai.com/v1/chat/completions";

    // API Key
    private String API_KEY;
    private final WebClient webClient;

    public ChatGPTController(Dotenv dotenv, WebClient.Builder webClientBuilder) {
        this.API_KEY = dotenv.get("API_KEY");
        if (API_KEY == null) {
            throw new IllegalArgumentException("API_KEY is required");
        } else { System.out.println("API_KEY: " + API_KEY.substring(0,5)); }

        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    @GetMapping("/chat")
    public Map<String, Object> getChat(@RequestParam String message) {
        ChatRequest chatRequest = new ChatRequest();    // Creating a new ChatRequest object
        chatRequest.setModel(GPT_MODEL_NAME);          // Setting the model to "gpt-3.5-turbo" to be used
        List<Message> lstMessage = new ArrayList<>();   // Creating a list of Message objects
        lstMessage.add(new Message("system", ""));  // Adding a system Message list
        lstMessage.add(new Message("user", message));   // Adding the user's message to the list
        chatRequest.setMessages(lstMessage);        // Setting the messages to the list in the chatRequest
        chatRequest.setN(1);                        // Setting the number of messages/completions to generate to 1
        chatRequest.setTemperature(TEMPERATURE);              // Setting the sampling temperature to 0
        chatRequest.setMaxTokens(MAX_TOKENS);               // Setting the maximum number of tokens to generate 10
        chatRequest.setStream(false);               // Setting whether to stream the results or not to false
        chatRequest.setPresencePenalty(PRESENCE_PENALTY);          // Setting the presence penalty to 1

        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(API_KEY))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        List<Choice> lst = response.getChoices();   // Extracting the choices from the response
        Usage usg = response.getUsage();            // Extracting the usage information from the response

        Map<String, Object> map = new HashMap<>();  // Creating a new HashMap object
        map.put("choices", lst);                    // Adding the choices to the map
        map.put("usage", usg);                      // Adding the usage information to the map

        return map;                                 // Returning the map
    }
}
