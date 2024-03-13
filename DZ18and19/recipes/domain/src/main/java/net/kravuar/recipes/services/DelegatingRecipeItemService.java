package net.kravuar.recipes.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.recipes.domain.AppComponent;
import net.kravuar.recipes.domain.RecipeItem;
import net.kravuar.recipes.domain.exceptions.RecipeItemNotFoundException;
import net.kravuar.recipes.repositories.RecipeItemRepository;

@RequiredArgsConstructor
@AppComponent
public class DelegatingRecipeItemService implements RecipeItemService {
    private final RecipeItemRepository recipeItemRepository;

    public RecipeItem findById(long recipeItemId) {
        return recipeItemRepository.findById(recipeItemId)
                .orElseThrow(RecipeItemNotFoundException::new);
    }
}