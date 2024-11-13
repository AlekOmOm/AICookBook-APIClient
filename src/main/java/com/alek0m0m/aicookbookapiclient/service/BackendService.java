package com.alek0m0m.aicookbookapiclient.service;

import com.alek0m0m.aicookbookapiclient.dto.RecipeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BackendService {


    private final WebClient webClient;

    @Autowired
    public BackendService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<RecipeDTO> getRecipeById(Long id) {
        Mono<RecipeDTO> recipeMono = webClient
                .get()
                .uri("/recipes/" + id)
                .retrieve()
                .bodyToMono(RecipeDTO.class);

        return recipeMono;
    }

    public Mono<List<RecipeDTO>> getAllRecipes() {
        Mono<List<RecipeDTO>> recipes = webClient
                .get()
                .uri("/recipes")
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToFlux(RecipeDTO.class)
                .collectList();

        return recipes;
    }
}
