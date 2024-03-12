package net.kravuar.recipes;

import lombok.RequiredArgsConstructor;
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
    private final RecipeMapper recipeMapper;

    @PostMapping
    RecipeDTO saveRecipe(@RequestBody RecipeCreationCommand recipeCreationCommand) {
        return recipeMapper.toDto(recipeService.create(recipeCreationCommand));
    }

    @GetMapping("/{id}")
    RecipeDTO findRecipeById(@PathVariable("id") long id) {
        return recipeMapper.toDto(recipeService.findById(id));
    }

    @GetMapping("/name/{name}")
    List<RecipeDTO> findRecipesByNameLike(@PathVariable("name") String name) {
        return recipeService.findByNameStartsWith(name).stream()
                .map(recipeMapper::toDto)
                .toList();
    }

    @GetMapping("/all")
    List<RecipeDTO> findAll() {
        return recipeService.findAll().stream()
                .map(recipeMapper::toDto)
                .toList();
    }

    @GetMapping("/cooking-time/{upperBound}")
    List<RecipeDTO> findRecipesByCookingTimeLessThan(@PathVariable("upperBound") Duration upperBound) {
        return recipeService.findByCookingTimeLessThan(upperBound).stream()
                .map(recipeMapper::toDto)
                .toList();
    }
}