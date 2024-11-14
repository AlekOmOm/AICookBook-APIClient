package com.alek0m0m.aicookbookapiclient.controller;

import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import com.alek0m0m.aicookbookapiclient.service.BackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class WebClientController {

    @Autowired
    BackendService backendService;

    @GetMapping
    public Mono<List<RecipeDTO>> getRecipes() {
        // Calls the BackendService to get all recipes and returns the result as a Mono<List<RecipeDTO>>
        Mono<List<RecipeDTO>> recipes = backendService.getAllRecipes();
        return backendService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public Mono<RecipeDTO> getRecipeById(Long id) {
        // Calls the BackendService to get a recipe by its ID and returns the result as a Mono<RecipeDTO>
        return backendService.getRecipeById(id);
    }


}
