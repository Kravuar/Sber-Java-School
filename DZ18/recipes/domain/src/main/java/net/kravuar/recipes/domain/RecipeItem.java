package net.kravuar.recipes.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecipeItem {
    private Long id;
    private Ingredient ingredient;
    private Recipe recipe;
    private double count;
    private CountUnit countUnit;

    public enum CountUnit {
        PIECE,
        GRAM,
        MILLIGRAM,
        MILLILITRES
    }
}
