package net.kravuar.recipes.services;

import lombok.RequiredArgsConstructor;
import net.kravuar.recipes.domain.AppComponent;
import net.kravuar.recipes.domain.Ingredient;
import net.kravuar.recipes.domain.exceptions.IngredientNotFoundException;
import net.kravuar.recipes.repositories.IngredientRepository;

import java.util.List;

@RequiredArgsConstructor
@AppComponent
class DelegatingIngredientService implements IngredientService {
    private final IngredientRepository ingredientRepository;

    @Override
    public Ingredient create(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Override
    public Ingredient findById(long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(IngredientNotFoundException::new);
    }

    @Override
    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    @Override
    public List<Ingredient> findByNameStartsWith(String name) {
        return ingredientRepository.findByNameStartsWith(name);
    }
}
