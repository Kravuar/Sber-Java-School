package net.kravuar.recipes;

import net.kravuar.recipes.domain.Ingredient;
import net.kravuar.recipes.domain.RecipeItem;

// This is needed as a workaround for jpa module
record RecipeItemDTO(
        Long id,
        Ingredient ingredient, // no need to DTO this
        double count,
        RecipeItem.CountUnit countUnit
) {
}
