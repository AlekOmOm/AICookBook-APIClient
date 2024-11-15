package com.alek0m0m.aicookbookapiclient;

import com.alek0m0m.aicookbookapiclient.dto.ChatRequest;
import com.alek0m0m.aicookbookapiclient.dto.ChatResponse;
import com.alek0m0m.aicookbookapiclient.dto.Choice;
import com.alek0m0m.aicookbookapiclient.dto.Message;
import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import com.alek0m0m.aicookbookapiclient.service.GenerateRecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GenerateRecipeServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private GenerateRecipeService generateRecipeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.baseUrl(any(String.class))).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void testGenerateRecipeFromIngredients() {
        // Mock the response from the AI API
        ChatResponse chatResponse = new ChatResponse();
        Choice choice = new Choice();
        Message message = new Message("system", "Mocked recipe content");

        choice.setMessage(message);
        chatResponse.setChoices(List.of(choice));

        when(responseSpec.bodyToMono(ChatResponse.class)).thenReturn(Mono.just(chatResponse));

        // Call the service method
        List<String> ingredients = List.of("ingredient1", "ingredient2");
        Mono<RecipeDTO> result = generateRecipeService.genCopy(ingredients);


        result.subscribe(recipe -> System.out.println("Received Recipe: " + recipe));


        // print result

        System.out.println(result);

    }
}