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


    /*
    initializer en empty RecipeDTO.
    Parser content(JSON) til et RecipeDTO objekt. Hvis parsing går godt bliver resipeDTO fyldt op
    med data fra content.
    Returnere den parsed RecipeDTO
     */
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
