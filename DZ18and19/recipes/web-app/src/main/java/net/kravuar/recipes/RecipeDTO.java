package net.kravuar.recipes;

import java.time.Duration;
import java.util.List;

// This is needed as a workaround for jpa module
record RecipeDTO(
        Long id,
        String name,
        Duration cookingTime,
        String description,
        List<RecipeItemDTO> recipeItems
) {
}
