package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.dto.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class GenerateRecipeService {
    // Constants for Settings:
        // model options: gpt-4o, gpt-4o-mini, gpt-4-turbo, gpt-4, and gpt-3.5-turbo
    private static final String GPT_MODEL_NAME = "gpt-4o";
    private static final double TEMPERATURE = 0.7;
    private static final int MAX_TOKENS = 500;
    private static final double PRESENCE_PENALTY = 1;

    // Constants for Recipe Generation:
    private static final int AMOUNT_OF_STORAGE_INGREDIENTS_IN_PROMPT = 3; // ingredients in prompt message
    private static final String NUMBER_OF_RECIPES_TO_GENERATE = "2"; // recipes to generate
    private static final int PERCENT_INGREDIENTS_FROM_STORAGE = 30; // % ingredients from storage

    //
        // note: static request limit (ie shared between all instances of this class)
    private static final int REQUEST_LIMIT = 2; // 1 request per 10 seconds
    private static final int RECIPE_GENERATION_TIMEOUT = 10000; // 10 seconds


    // API Key
    private final String API_KEY; // set in .env at root of project
    private final ParseResponseService parseResponseService;
    private final RecipeParser recipeParser;

    @Autowired
    public GenerateRecipeService(Dotenv dotenv, ParseResponseService parseResponseService, RecipeParser recipeParser) {
        this.API_KEY = dotenv.get("API_KEY");
        this.recipeParser = recipeParser;
        System.out.println("API_KEY: " + API_KEY.substring(0,5));
        this.parseResponseService = parseResponseService;
    }

    private static int counter = 0;
    private static long timer = System.currentTimeMillis();

    private boolean limitReached() {
        counter++;
        if (System.currentTimeMillis() - timer > RECIPE_GENERATION_TIMEOUT) {
            counter --;
            timer = System.currentTimeMillis();
        }
        return counter > REQUEST_LIMIT;
    }


    /*
   String.join joiner listen med ingredienser til en komma separeret string.
   String.format laver en message til AI.
    */

    public Mono<List<RecipeDTOSimple>> generateRecipeFromIngredients(List<IngredientDTO> ingredients) {
        if (limitReached()) {
            return Mono.error(new RuntimeException("Limit reached"));
        }
        if (ingredients == null || ingredients.isEmpty()) {
            assert ingredients != null;
            ingredients.addAll(List.of(
                    new IngredientDTO(0L, "Onion", 1, "kg"),
                    new IngredientDTO(0L, "Tomato", 1, "kg"),
                    new IngredientDTO(0L, "Potato", 1, "kg")
            ));
        }
        ingredients = ingredients.subList(0, AMOUNT_OF_STORAGE_INGREDIENTS_IN_PROMPT);

        String userIngredients = ingredients.stream()
                .map(IngredientDTO::getName)
                .collect(Collectors.joining(","));


        String promptMessage = createPromptMessage(userIngredients);

        ChatRequest chatRequest = new ChatRequest()
                .setModel(GPT_MODEL_NAME)
                .setMessages(List.of(
                        new Message("system", "You are a helpful recipe assistant."),
                        new Message("user", promptMessage)
                ))
                .setN(3)
                .setTemperature(TEMPERATURE)
                .setMaxTokens(MAX_TOKENS)
                .setPresencePenalty(PRESENCE_PENALTY);

        // return Mono.just(recipeParser.parseRecipe()); // TODO remove this line later

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
                    return Mono.just(recipeParser.parseRecipe(content)
                    );
                });


    }




    private String createPromptMessage(String userIngredients) {
        return String.format(
                "Optimize to use the Ingredients in Storage: %s. Provide " + NUMBER_OF_RECIPES_TO_GENERATE + " recipes. "
                        + "At least " + PERCENT_INGREDIENTS_FROM_STORAGE + " percent of the Recipe ingredients must be from storage. "
                        + "Respond precisely in JSON format of Recipe: "
                        + "{"
                            + "\"name\": \"Recipe Name\", "
                            + "\"instructions\": \"Recipe instructions\", "
                            + "\"ingredients\": ["
                            + "    {"
                            + "        \"id\": \"0\", "
                            + "        \"name\": \"fx Onion\", "
                            + "        \"amount\": \"int: value\", "
                            + "        \"unit\": \"String: kg, g, l or ml\""
                            + "    }"
                            + "]"
                        + "}",
                "ingredients in storage: " +
                userIngredients
        );
    }

    /*
     {
   "id": 0,
  "name": "string",
  "instructions": "string",
  "ingredients": [
    {
      "id": 0,
      "name": "string",
      "amount": int,
      "unit": "string"
    }
  ]
  }
     */


    // ----------


    public Mono<String> genCopy (List < String > ingredients) {

        System.out.println(API_KEY.substring(0, 5));

        String userIngredients = String.join(",", ingredients);
        String promtMessage = String.format(
                "I have these ingredients: %s. please provide recips i can make with only these ingredients. "
                        + " also, suggest recipes where i have at least " + PERCENT_INGREDIENTS_FROM_STORAGE + " percent of the ingredients, and indicate which ingredients i am missing.",
                userIngredients
        );

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        chatRequest.setMessages(List.of(
                new Message("system", "you are a helpful recipe assistant."),
                new Message("user", promtMessage)
        ));

        chatRequest.setN(3);
        chatRequest.setTemperature(1.0);
        chatRequest.setMaxTokens(150);
        chatRequest.setPresencePenalty(1.0);

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


}
