package com.alek0m0m.aicookbookapiclient.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import javax.annotation.processing.Generated;
import java.util.LinkedHashMap;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "instructions", "tags", "servings", "prepTime", "cookTime", "totalTime"})
@Generated("jsonschema2pojo")
public class RecipeDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("tags")
    private String tags;

    @JsonProperty("servings")
    private int servings;

    @JsonProperty("prepTime")
    private int prepTime;

    @JsonProperty("cookTime")
    private int cookTime;

    @JsonProperty("totalTime")
    private int totalTime;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("instructions")
    public String getInstructions() {
        return instructions;
    }

    @JsonProperty("instructions")
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @JsonProperty("tags")
    public String getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(String tags) {
        this.tags = tags;
    }

    @JsonProperty("servings")
    public int getServings() {
        return servings;
    }

    @JsonProperty("servings")
    public void setServings(int servings) {
        this.servings = servings;
    }

    @JsonProperty("prepTime")
    public int getPrepTime() {
        return prepTime;
    }

    @JsonProperty("prepTime")
    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    @JsonProperty("cookTime")
    public int getCookTime() {
        return cookTime;
    }

    @JsonProperty("cookTime")
    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    @JsonProperty("totalTime")
    public int getTotalTime() {
        return totalTime;
    }

    @JsonProperty("totalTime")
    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
