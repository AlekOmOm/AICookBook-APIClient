package com.alek0m0m.aicookbookapiclient.dto;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(as = RecipeDTO.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "instructions",
        "tags",
        "servings",
        "prep_time",
        "cook_time",
        "total_time"
})
public class RecipeDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("tags")
    private String tags;

    @JsonProperty("Servings")
    private int servings;

    @JsonProperty("prep_time")
    private int prepTime;

    @JsonProperty("cook_time")
    private int cookTime;

    @JsonProperty("total_time")
    private int totalTime;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
