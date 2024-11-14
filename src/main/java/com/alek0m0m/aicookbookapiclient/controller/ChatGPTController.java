package com.alek0m0m.aicookbookapiclient.controller;

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

    @Value("${OPENAI_APIKEY}")
    private String openapiKey;

    private final WebClient webClient;

    public ChatGPTController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    @GetMapping("/chat")
    public Map<String, Object> getChat(@RequestParam String message) {
        ChatRequest chatRequest = new ChatRequest();    // Creating a new ChatRequest object
        chatRequest.setModel("gpt-3.5-turbo");          // Setting the model to "gpt-3.5-turbo" to be used
        List<Message> lstMessage = new ArrayList<>();   // Creating a list of Message objects
        lstMessage.add(new Message("system", ""));  // Adding a system Message list
        lstMessage.add(new Message("user", message));   // Adding the user's message to the list
        chatRequest.setMessages(lstMessage);        // Setting the messages to the list in the chatRequest
        chatRequest.setN(1);                        // Setting the number of messages/completions to generate to 1
        chatRequest.setTemperature(0);              // Setting the sampling temperature to 0
        chatRequest.setMaxtokens(30);               // Setting the maximum number of tokens to generate 10
        chatRequest.setStream(false);               // Setting whether to stream the results or not to false
        chatRequest.setPresencePenalty(1);          // Setting the presence penalty to 1

        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(openapiKey))
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
