package net.kravuar.recipes.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.recipes.domain.AppComponent;
import net.kravuar.recipes.domain.Recipe;
import net.kravuar.recipes.domain.commands.RecipeCreationCommand;
import net.kravuar.recipes.domain.exceptions.RecipeNotFoundException;
import net.kravuar.recipes.repositories.RecipeRepository;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@AppComponent
public class DelegatingRecipeService implements RecipeService {
    protected final RecipeRepository recipeRepository;

    @Override
    public Recipe create(RecipeCreationCommand command) {
        Recipe recipe = new Recipe();
        recipe.setName(command.name());
        recipe.setDescription(command.description());
        recipe.setCookingTime(command.cookingTime());

        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe findById(long id) {
        return recipeRepository.findById(id)
                .orElseThrow(RecipeNotFoundException::new);
    }

    @Override
    public List<Recipe> findByNameStartsWith(String name) {
        return recipeRepository.findByNameStartsWith(name);
    }

    @Override
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> findByCookingTimeLessThan(Duration upperBound) {
        return recipeRepository.findByCookingTimeLessThan(upperBound);
    }
}