package net.kravuar.recipes.repositories;

import net.kravuar.recipes.domain.Ingredient;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    void deleteAll();

    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findById(long id);

    List<Ingredient> findAll();

    List<Ingredient> findByNameStartsWith(String name);
}
