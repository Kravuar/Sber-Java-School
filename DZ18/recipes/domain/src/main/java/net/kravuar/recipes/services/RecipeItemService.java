package net.kravuar.recipes.services;

import net.kravuar.recipes.domain.RecipeItem;

public interface RecipeItemService {
    RecipeItem findById(long recipeItemId);
}
