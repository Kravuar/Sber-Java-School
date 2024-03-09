package net.kravuar.recipes.services;

import net.kravuar.recipes.domain.Recipe;
import net.kravuar.recipes.domain.commands.RecipeCreationCommand;

import java.time.Duration;
import java.util.List;

public interface RecipeService {
    Recipe create(RecipeCreationCommand recipe);

    Recipe findById(long id);

    List<Recipe> findByNameStartsWith(String name);

    List<Recipe> findAll();

    List<Recipe> findByCookingTimeLessThan(Duration upperBound);
}
