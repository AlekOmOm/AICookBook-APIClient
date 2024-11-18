package com.alek0m0m.aicookbookapiclient.service;
import com.alek0m0m.aicookbookapiclient.dto.RecipeDTOSimple;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RecipeParser {

    private final ObjectMapper objectMapper;
    private final ParseResponseService parseResponseService;

    public RecipeParser(ObjectMapper objectMapper, ParseResponseService parseResponseService) {
        this.objectMapper = objectMapper;
        this.parseResponseService = parseResponseService;
    }

    public List<RecipeDTOSimple> parseRecipe(String content) {
        if (content == null) {
            content = parseResponseService.getExampleJSONContent();
        }

        List<RecipeDTOSimple> recipes = new ArrayList<>();

        try {
            // Preprocess content
            String cleanedContent = preprocessContent(content);

            // Extract complete JSON objects
            List<String> jsonObjects = extractCompleteJsonObjects(cleanedContent);

            // Parse each JSON object
            for (String jsonObject : jsonObjects) {
                try {
                    JsonNode recipeNode = objectMapper.readTree(jsonObject);
                    if (isCompleteRecipe(recipeNode)) {
                        RecipeDTOSimple recipe = objectMapper.treeToValue(recipeNode, RecipeDTOSimple.class);
                        recipes.add(recipe);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing a recipe: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.err.println("Error processing content: " + e.getMessage());
            e.printStackTrace();
        }

        return recipes;
    }


    private List<String> extractCompleteJsonObjects(String json) {
        List<String> jsonObjects = new ArrayList<>();
        int length = json.length();
        boolean inString = false;
        char prevChar = 0;
        int openBraces = 0;
        int start = -1;

        for (int i = 0; i < length; i++) {
            char currentChar = json.charAt(i);

            if (currentChar == '"' && prevChar != '\\') {
                inString = !inString;
            }

            if (!inString) {
                if (currentChar == '{') {
                    if (openBraces == 0) {
                        // Start of a new JSON object
                        start = i;
                    }
                    openBraces++;
                } else if (currentChar == '}') {
                    openBraces--;
                    if (openBraces == 0 && start != -1) {
                        // End of a JSON object
                        String jsonObject = json.substring(start, i + 1);
                        jsonObjects.add(jsonObject);
                        start = -1;
                    }
                }
            }

            prevChar = currentChar;
        }

        System.out.println("Extracted JSON objects: " + jsonObjects.size());
        return jsonObjects;
    }

    private String preprocessContent(String content) {
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7); // Remove ```json
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3); // Remove trailing ```
        }
        return content.trim();
    }

    private boolean isCompleteRecipe(JsonNode recipeNode) {
        return recipeNode.has("name") &&
                recipeNode.has("instructions") &&
                recipeNode.has("ingredients") &&
                recipeNode.get("ingredients").isArray();
    }


}
