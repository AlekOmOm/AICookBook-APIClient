package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.alek0m0m.aicookbookapiclient.Command.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BackendService {



    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public BackendService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
        this.objectMapper = objectMapper;
    }

    public Mono<RecipeDTO> getRecipeById(Long id) {
        Mono<RecipeDTO> recipeMono = webClient
                .get()
                .uri("/api/recipes/" + id)
                .retrieve()
                .bodyToMono(RecipeDTO.class);

        return recipeMono;
    }

    public Mono<List<RecipeDTO>> getAllRecipes() {
        Mono<List<RecipeDTO>> recipes = webClient
                .get()
                .uri("/api/recipes")
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToFlux(RecipeDTO.class)
                .collectList();

        return recipes;
    }

    public Mono<List<IngredientDTO>> getAllIngredients() {
        return webClient
                .get()
                .uri("/api/ingredients")
                .retrieve()
                .bodyToFlux(IngredientDTO.class)
                .collectList();
    }


    public List<RecipeDTO> saveRecipes(List<RecipeDTOSimple> recipes) {
        for (RecipeDTOSimple recipe : recipes) {
            webClient
                    .post()
                    .uri("/api/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(recipe), RecipeDTOSimple.class)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
        return getSavedRecipes(recipes);
    }

    private List<RecipeDTO> getSavedRecipes(List<RecipeDTOSimple> recipes) {
        List<RecipeDTO> justSavedRecipes = new ArrayList<>();
        List<RecipeDTO> savedRecipes = getAllRecipes().block();

        for (RecipeDTOSimple recipe : recipes) {
            Optional<RecipeDTO> savedRecipe = savedRecipes.stream()
                    .filter(r -> r.getName().equals(recipe.getName()))
                    .findFirst();
            if (savedRecipe.isPresent()) {
                justSavedRecipes.add(savedRecipe.get());
            } else {
                System.out.println("Recipe not saved: " + recipe.getName());
            }
        }
        return justSavedRecipes;
    }
}
