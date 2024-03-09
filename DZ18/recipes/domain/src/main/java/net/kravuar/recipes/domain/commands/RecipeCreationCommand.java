package net.kravuar.recipes.domain.commands;

import java.time.Duration;
import java.util.List;

public record RecipeCreationCommand(
        String name,
        Duration cookingTime,
        String description,
        List<RecipeItemCreationCommand> recipeItems
) {
}
