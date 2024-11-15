package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.Command.ParseRecipeCommand;
import com.alek0m0m.aicookbookapiclient.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@Service
public class GenerateRecipeService {

    @Value("${openai.api.key}")
    private String apiKey;


    /*
   String.join joiner listen med ingredienser til en komma separeret string.
   String.format laver en message til AI.
    */
    public Mono<RecipeDTO> generateRecipeFromIngredients(List<String> ingredients) {

        String userIngredients = String.join(",", ingredients);
        String promtMessage = String.format(
                "I have these ingredients: %s. please provide recips i can make with only these ingredients. "
                        +" also, suggest recipes where i have at least 50 percent of the ingredients, and indicate which ingredients i am missing.",
                userIngredients
        );

        /*
        Specifisere versionen af ChatGPT og tilføjer 2 beskeder, en system prmt som giver context
        og en user promt til recipe request.
         */
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        chatRequest.setMessages(List.of(
                new Message("system", "you are a helpful recipe assistant."),
                new Message("user", promtMessage)
        ));


        /*
        Beder om 3 completions, sætter randomnes i chattens response,
        sætter en limmit på lænden af den response den giver,
        PresencePenalty er til hvis der er repeated ingredienser
         */
        chatRequest.setN(3);
        chatRequest.setTemperature(1);
        chatRequest.setMaxTokens(150);
        chatRequest.setPresencePenalty(1);


        /*
        Konfigurere en ny WebClient til OpenAI API endpoint.
        Sætter HTTP metoden til POST.
        Sætter JSON som request content typen.
        Sætter chatRequest som body.
        Executer request og mapper den response to ChatResponse.
        Processere den response man har fået asynkront,
            checker får null og retriever den første response,
            extracter message content(recipe).
         */
        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
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


                    /*
                    Bruger en custom command til at parse content og returnere et RecipeDTO object
                     */
                    ParseRecipeCommand parseCommand = new ParseRecipeCommand(content);
                    return Mono.just(parseCommand.execute());
                });
    }
}
