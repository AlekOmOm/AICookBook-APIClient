package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import org.springframework.stereotype.Service;

@Service
public class ParseResponseService {



    public RecipeDTO parseRecipe(String content) {

        System.out.println("Parsing recipe from content: " + content);

        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(1L);

        return recipeDTO;

    }
}
