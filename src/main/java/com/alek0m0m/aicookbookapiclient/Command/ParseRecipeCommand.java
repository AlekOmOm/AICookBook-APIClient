package com.alek0m0m.aicookbookapiclient.Command;

import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseRecipeCommand implements Command<RecipeDTO> {

    private final String content;
    private final ObjectMapper objectMapper;

    public ParseRecipeCommand(String content) {
        this.content = content;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public RecipeDTO execute() {
        RecipeDTO recipeDTO = new RecipeDTO();
        try {
            recipeDTO = objectMapper.readValue(content, RecipeDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipeDTO;
    }
}
