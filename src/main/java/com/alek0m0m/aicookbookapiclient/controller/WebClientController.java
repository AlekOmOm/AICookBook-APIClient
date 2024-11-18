package com.alek0m0m.aicookbookapiclient.controller;

import com.alek0m0m.aicookbookapiclient.dto.IngredientDTO;
import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import com.alek0m0m.aicookbookapiclient.dto.RecipeDTOSimple;
import com.alek0m0m.aicookbookapiclient.service.BackendService;
import com.alek0m0m.aicookbookapiclient.service.GenerateRecipeService;
import com.alek0m0m.aicookbookapiclient.service.ParseResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/recipes")
public class WebClientController {

    @Autowired
    BackendService backendService;

    @Autowired
    GenerateRecipeService generateRecipeService;
    @Autowired
    private ParseResponseService parseResponseService;

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

    @GetMapping("/generate-recipe") // url: /api/recipes/generate-recipe
    public Mono<List<RecipeDTO>> generateRecipeFromIngredients(/*@RequestBody List<String> ingredients*/) {
        System.out.println();
        System.out.println("fetch /generate-recipe called");
        System.out.println();
        List<IngredientDTO> combinedIngredients = backendService.getAllIngredients().block();

        Mono<List<RecipeDTOSimple>> recipes =  generateRecipeService.generateRecipeFromIngredients(combinedIngredients);
        debug(recipes);

        List<RecipeDTO> dtos = backendService.saveRecipes(recipes.block());
        return Mono.just(dtos);
    }




    // ----------------- Debugging -----------------

    private void debug(Mono<List<RecipeDTOSimple>> recipes) {
        System.out.println();
        System.out.println("debug");
        System.out.println("END fetch /generate-recipe");
        System.out.println(" " + recipes.block());
        try {
            assert recipes.block().equals(parseResponseService.getLatestRecipes());
        } catch (AssertionError e) {
            System.out.println("ERROR: Recipes generated do not match the latest parsed recipes.");
            e.printStackTrace();
        }

        System.out.println(" ");
        System.out.println("END responding to /generate-recipe");
    }

}
