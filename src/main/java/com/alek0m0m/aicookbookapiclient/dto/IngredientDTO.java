package com.alek0m0m.aicookbookapiclient.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(as = IngredientDTO.class)
@JsonPropertyOrder({
        "id",
        "name",
        "amount",
        "unit"
})
public class IngredientDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("amount")
    private int amount;

    @JsonProperty("unit")
    private String unit;


}
