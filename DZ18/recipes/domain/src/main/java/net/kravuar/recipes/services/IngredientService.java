package net.kravuar.recipes.services;

import net.kravuar.recipes.domain.Ingredient;

import java.util.List;

public interface IngredientService {
    Ingredient create(Ingredient ingredient);

    Ingredient findById(long id);

    List<Ingredient> findAll();

    List<Ingredient> findByNameStartsWith(String name);
}
