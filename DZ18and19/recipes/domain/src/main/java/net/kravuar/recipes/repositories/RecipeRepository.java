package net.kravuar.recipes.repositories;

import net.kravuar.recipes.domain.Recipe;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    void deleteAll();

    Recipe save(Recipe recipe);

    Optional<Recipe> findById(long id);

    List<Recipe> findByNameStartsWith(String name);

    List<Recipe> findAll();

    List<Recipe> findByCookingTimeLessThan(Duration upperBound);
}
