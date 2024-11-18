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
    private static final double TEMPERATURE = 1; // 0.5 - 1.0 (0.5 is more deterministic, 1.0 is more creative)
    private static final int MAX_TOKENS = 500;
    private static final double PRESENCE_PENALTY = 1;

    // Constants for Recipe Generation:
    private static final int AMOUNT_OF_STORAGE_INGREDIENTS_IN_PROMPT = 4; // ingredients in prompt message
    private static final String NUMBER_OF_RECIPES_TO_GENERATE = "1"; // recipes to generate
    private static final int PERCENT_INGREDIENTS_FROM_STORAGE = 25; // % ingredients from storage

    //
        // note: static request limit (ie shared between all instances of this class)
    private static final int REQUEST_LIMIT = 1; // 1 request per 10 seconds
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

        String promptMessage = createPromptMessage(getUserIngredients(ingredients));

        System.out.println();
        System.out.println("promptMessage: " + promptMessage);
        System.out.println();

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

    private String getUserIngredients(List<IngredientDTO> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            assert ingredients != null;
            ingredients.addAll(List.of(
                    new IngredientDTO(0L, "Onion", 1, "kg"),
                    new IngredientDTO(0L, "Tomato", 1, "kg"),
                    new IngredientDTO(0L, "Potato", 1, "kg")
            ));
        }

        ingredients = getRandomIngredients(ingredients, AMOUNT_OF_STORAGE_INGREDIENTS_IN_PROMPT);

        String userIngredients = ingredients.stream()
                .map(IngredientDTO::getName)
                .collect(Collectors.joining(","));
        return userIngredients;
    }

    private List<IngredientDTO> getRandomIngredients(List<IngredientDTO> ingredients, int amountOfStorageIngredientsInPrompt) {
        System.out.println();
        System.out.println("getRandomIngredients");
        System.out.println("ingredients: " + ingredients.size());

        int[] randomIndices = new int[amountOfStorageIngredientsInPrompt];
        List<Integer> prevInts = new java.util.ArrayList<>();
        for (int i = 0; i < amountOfStorageIngredientsInPrompt; i++) {
            int ival = (int) (Math.random() * ingredients.size());
            if (prevInts.contains(ival)) {
                i--;
                continue;
            } else {
                prevInts.add(ival);
                randomIndices[i] = ival;
            }
        }

        List<IngredientDTO> randomIngredients = new java.util.ArrayList<>();
        for (int randomIndex : randomIndices) {
            randomIngredients.add(ingredients.get(randomIndex));
        }
        System.out.println("randomIngredients: " + randomIngredients.size());
        System.out.println("randomIngredients: " + randomIngredients);
        System.out.println();
        return randomIngredients;
    }


    private String createPromptMessage(String userIngredients) {
        return String.format(
                "Input the following storage ingredients: %s. Please provide " + NUMBER_OF_RECIPES_TO_GENERATE + " creative recipes. "
                        + "Create good creative Recipe use some ingredients from storage, but feel free to use ingredients I can go shop for. "
                        + "The response must follow the JSON format for a Recipe as shown below: "
                        + "{"
                        + "\"name\": \"Insert creative recipe name here (String)\", "
                        + "\"instructions\": \"Detailed cooking instructions (String) \", "
                        + "\"ingredients\": ["
                        + "    {"
                        + "        \"id\": \"0 (long) \", "
                        + "        \"name\": \"Name of ingredient (String)\", "
                        + "        \"amount\": \"Specify quantity here (Integer) \", "
                        + "        \"unit\": \"Specify unit like kg, g, l or ml here (String)\""
                        + "    }"
                        + "]"
                        + "}",
                "ingredients in storage: " +
                userIngredients
                + " be sure to use the correct data types for the values in the JSON format."
                + " thanks for the help and be creative!"
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
