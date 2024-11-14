package com.alek0m0m.aicookbookapiclient.controller;

import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import com.alek0m0m.aicookbookapiclient.service.BackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class WebClientController {

    @Autowired
    BackendService backendService;

    @GetMapping("")
    public Mono<List<RecipeDTO>> getRecipes() {
        // Calls the BackendService to get all recipes and returns the result as a Mono<List<RecipeDTO>>
        Mono<List<RecipeDTO>> recipes = backendService.getAllRecipes();
        return backendService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public Mono<RecipeDTO> getRecipeById(@PathVariable("id") Long id) {
        // Calls the BackendService to get a recipe by its ID and returns the result as a Mono<RecipeDTO>
        return backendService.getRecipeById(id);
    }

    @PostMapping("/generate-recipe")
    public Mono<RecipeDTO> generateRecipeFromIngredients(@RequestBody List<String> ingredients) {
        return backendService.generateRecipeFromIngredients(ingredients);
    }

}
