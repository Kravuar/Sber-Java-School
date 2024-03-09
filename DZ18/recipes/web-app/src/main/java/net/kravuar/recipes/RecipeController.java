package net.kravuar.recipes;

import lombok.RequiredArgsConstructor;
import net.kravuar.recipes.domain.Recipe;
import net.kravuar.recipes.domain.commands.RecipeCreationCommand;
import net.kravuar.recipes.services.RecipeService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recipe")
class RecipeController {
    private final RecipeService recipeService;

    @PostMapping
    Recipe saveRecipe(@RequestBody RecipeCreationCommand recipeCreationCommand) {
        return recipeService.create(recipeCreationCommand);
    }

    @GetMapping("/{id}")
    Recipe findRecipeById(@PathVariable("id") long id) {
        return recipeService.findById(id);
    }

    @GetMapping("/name/{name}")
    List<Recipe> findRecipesByNameLike(@PathVariable("name") String name) {
        return recipeService.findByNameStartsWith(name);
    }

    @GetMapping("/all")
    List<Recipe> findAll() {
        return recipeService.findAll();
    }

    @GetMapping("/cooking-time/{upperBound}")
    List<Recipe> findRecipesByCookingTimeLessThan(@PathVariable("upperBound") Duration upperBound) {
        return recipeService.findByCookingTimeLessThan(upperBound);
    }
}