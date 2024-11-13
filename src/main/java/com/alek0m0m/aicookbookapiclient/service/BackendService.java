package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.alek0m0m.aicookbookapiclient.Command.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BackendService {

    @Value("${openai.api.key}")
    private String apiKey;


    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public BackendService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
        this.objectMapper = objectMapper;
    }

    public Mono<RecipeDTO> getRecipeById(Long id) {
        Mono<RecipeDTO> recipeMono = webClient
                .get()
                .uri("/recipes/" + id)
                .retrieve()
                .bodyToMono(RecipeDTO.class);

        return recipeMono;
    }

    public Mono<List<RecipeDTO>> getAllRecipes() {
        Mono<List<RecipeDTO>> recipes = webClient
                .get()
                .uri("/recipes")
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToFlux(RecipeDTO.class)
                .collectList();

        return recipes;
    }

    public Mono<RecipeDTO> generateRecipeFromIngredients(List<String> ingredients) {
        String userIngredients = String.join(",", ingredients);
        String promtMessage = String.format(
                "I have these ingredients: %s. please provide recips i can make with only these ingredients. "
                +" also, suggest recipes where i have at least 50% of the ingredients, and indicate which ingredients i am missing.",
                userIngredients
        );

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        chatRequest.setMessages(List.of(
                new Message("system", "you are a helpful recipe assistant."),
                new Message("user", promtMessage)
        ));

        chatRequest.setN(3);
        chatRequest.setTemperature(1);
        chatRequest.setMaxTokens(150);
        chatRequest.setPresencePenalty(1);

        return WebClient.builder()
                .baseUrl("http://api.openai.com/v1/chat/completions")
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(apiKey))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .flatMap(response -> {
                    Choice firstChoice = Optional.ofNullable(response.getChoices().get(0)).orElse(null);
                    String content = firstChoice.getMessage().getContent();

                    ParseRecipeCommand parseCommand = new ParseRecipeCommand(content);
                    return Mono.just(parseCommand.execute());
                });
    }


}
