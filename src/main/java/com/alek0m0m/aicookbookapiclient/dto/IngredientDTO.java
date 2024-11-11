package com.alek0m0m.aicookbookapiclient.dto;


import com.alek0m0m.aicookbookapiclient.model.Ingredient;
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
public class IngredientDTO {

    private Long id;
    private String name;
    private int amount;
    private String unit;


    public Ingredient toEntity() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(getId());
        ingredient.setName(getName());
        ingredient.setAmount(getAmount());
        ingredient.setUnit(getUnit());
        return ingredient;
    }
}
