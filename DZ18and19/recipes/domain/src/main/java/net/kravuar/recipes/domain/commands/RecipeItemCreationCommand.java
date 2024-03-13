package net.kravuar.recipes.domain.commands;

import net.kravuar.recipes.domain.RecipeItem;

public record RecipeItemCreationCommand(
        Long ingredientId,
        double count,
        RecipeItem.CountUnit unit
) {
}
