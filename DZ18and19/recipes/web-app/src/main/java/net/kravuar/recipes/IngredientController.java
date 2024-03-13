package net.kravuar.recipes;

import lombok.RequiredArgsConstructor;
import net.kravuar.recipes.domain.Ingredient;
import net.kravuar.recipes.services.IngredientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ingredient")
class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping
    Ingredient saveIngredient(@RequestBody Ingredient ingredient) {
        return ingredientService.create(ingredient);
    }

    @GetMapping("/{id}")
    Ingredient findIngredientById(@PathVariable("id") long id) {
        return ingredientService.findById(id);
    }

    @GetMapping("/all")
    List<Ingredient> findAll() {
        return ingredientService.findAll();
    }

    @GetMapping("/name/{name}")
    List<Ingredient> findIngredientsByName(@PathVariable("name") String name) {
        return ingredientService.findByNameStartsWith(name);
    }
}