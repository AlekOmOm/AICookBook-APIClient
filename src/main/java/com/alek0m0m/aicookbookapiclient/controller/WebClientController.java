package com.alek0m0m.aicookbookapiclient.controller;

import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import com.alek0m0m.aicookbookapiclient.service.BackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class WebClientController {

    @Autowired
    BackendService backendService;

    @GetMapping("/api/recipes")
    public Mono<List<RecipeDTO>> getRecipes() {
        // Calls the BackendService to get all recipes and returns the result as a Mono<List<RecipeDTO>>
        Mono<List<RecipeDTO>> recipes = backendService.getAllRecipes();
        return backendService.getAllRecipes();
    }

    @GetMapping("/api/recipes/{id}")
    public Mono<RecipeDTO> getRecipeById(Long id) {
        // Calls the BackendService to get a recipe by its ID and returns the result as a Mono<RecipeDTO>
        return backendService.getRecipeById(id);
    }

    @PostMapping("/recipes/generate")
    public Mono<RecipeDTO> generateRecipeFromIngredients(@RequestBody List<String> ingredients) {
        return backendService.generateRecipeFromIngredients(ingredients);
    }

}
