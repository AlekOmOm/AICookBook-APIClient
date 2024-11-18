package com.alek0m0m.aicookbookapiclient.dto;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonDeserialize(as = RecipeDTOSimple.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({

        "name",
        "instructions",
        "ingredients"
})
public class RecipeDTOSimple {

    @JsonProperty("name")
    private String name;

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("ingredients")
    private List<IngredientDTO> ingredients;
}

