package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import org.springframework.stereotype.Service;

@Service
public class ParseResponseService {

    public RecipeDTO parseRecipe(String content) {

        System.out.println("Parsing recipe from content: " + content);

        RecipeDTO recipeDTO = new RecipeDTO();

        // need to go through content to find JSON Recipe object
        // then parse it into RecipeDTO

        String json = parseGetRecipeJSON(content);

        System.out.println("JSON: " + json);

        mapJsonToRecipeDTO(json, recipeDTO);

        return recipeDTO;

    }

    private static void mapJsonToRecipeDTO(String json, RecipeDTO recipeDTO) {
        String[] parts = json.split(",");

        for (String part : parts) {
            if (part.contains("id")) {
                recipeDTO.setId(Long.parseLong(part.split(":")[1]));
            } else if (part.contains("name")) {
                recipeDTO.setName(part.split(":")[1]);
            } else if (part.contains("instructions")) {
                recipeDTO.setInstructions(part.split(":")[1]);
            } else if (part.contains("tags")) {
                recipeDTO.setTags(part.split(":")[1]);
            } else if (part.contains("servings")) {
                recipeDTO.setServings(Integer.parseInt(part.split(":")[1]));
            } else if (part.contains("prepTime")) {
                recipeDTO.setPrepTime(Integer.parseInt(part.split(":")[1]));
            } else if (part.contains("cookTime")) {
                recipeDTO.setCookTime(Integer.parseInt(part.split(":")[1]));
            } else if (part.contains("totalTime")) {
                recipeDTO.setTotalTime(Integer.parseInt(part.split(":")[1]));
            }
        }
    }

    private String parseGetRecipeJSON(String content) {

        StringBuilder json = new StringBuilder();

        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '{') {
                json.append(content.charAt(i));
                i++;
                while (content.charAt(i) != '}') {
                    json.append(content.charAt(i));
                    i++;
                }
                json.append(content.charAt(i));
                break;
            }
        }

        return json.toString();



    }
}
