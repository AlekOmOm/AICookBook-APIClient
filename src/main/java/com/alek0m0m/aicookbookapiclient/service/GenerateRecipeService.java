package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.Command.ParseRecipeCommand;
import com.alek0m0m.aicookbookapiclient.dto.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@Service
public class GenerateRecipeService {

    private final String API_KEY;
    private final ParseResponseService parseResponseService;

    @Autowired
    public GenerateRecipeService(Dotenv dotenv, ParseResponseService parseResponseService) {
        this.API_KEY = dotenv.get("API_KEY");

        System.out.println("API_KEY: " + API_KEY.substring(0,5));
        this.parseResponseService = parseResponseService;
    }

    public Mono<String> genCopy (List < String > ingredients) {

        System.out.println(API_KEY.substring(0, 5));

        String userIngredients = String.join(",", ingredients);
        String promtMessage = String.format(
                "I have these ingredients: %s. please provide recips i can make with only these ingredients. "
                        + " also, suggest recipes where i have at least 50 percent of the ingredients, and indicate which ingredients i am missing.",
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
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(API_KEY))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .flatMap(response -> {
                    Choice firstChoice = Optional.ofNullable(response.getChoices().get(0)).orElse(null);

                    String content = firstChoice.getMessage().getContent();
                    return Mono.just(new String(content)
                    );
                });
    }







    /*
   String.join joiner listen med ingredienser til en komma separeret string.
   String.format laver en message til AI.
    */
    public Mono<RecipeDTO> generateRecipeFromIngredients(List<String> ingredients) {


        String userIngredients = String.join(",", ingredients);
        String promtMessage = String.format(
                "I have these ingredients: %s. please provide recips i can make with only these ingredients. "
                        + " also, suggest recipes where i have at least 50 percent of the ingredients, and indicate which ingredients i am missing.",
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
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(API_KEY))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .flatMap(response -> {
                    Choice firstChoice = Optional.ofNullable(response.getChoices().get(0)).orElse(null);

                    String content = firstChoice.getMessage().getContent();



                    return Mono.just(parseResponseService.parseRecipe(content)
                    );
                });
    }



}
