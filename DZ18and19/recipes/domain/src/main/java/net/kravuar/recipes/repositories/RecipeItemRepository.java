package net.kravuar.recipes.repositories;

import net.kravuar.recipes.domain.RecipeItem;

import java.util.List;
import java.util.Optional;

public interface RecipeItemRepository {
    void deleteAll();

    RecipeItem save(RecipeItem recipeItem);

    Optional<RecipeItem> findById(long recipeItemId);

    List<RecipeItem> findByRecipeId(long recipeId);
}
