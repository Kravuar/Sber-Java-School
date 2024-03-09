package net.kravuar.recipes.persistence;

import jakarta.transaction.Transactional;
import net.kravuar.recipes.domain.AppComponent;
import net.kravuar.recipes.domain.Recipe;
import net.kravuar.recipes.domain.RecipeItem;
import net.kravuar.recipes.domain.commands.RecipeCreationCommand;
import net.kravuar.recipes.domain.exceptions.IngredientNotFoundException;
import net.kravuar.recipes.repositories.IngredientRepository;
import net.kravuar.recipes.repositories.RecipeRepository;
import net.kravuar.recipes.services.DelegatingRecipeService;

import java.util.List;

@AppComponent
class JDBCRecipeService extends DelegatingRecipeService {
    private final JDBCRecipeItemRepository recipeItemRepository;
    private final IngredientRepository ingredientRepository;

    public JDBCRecipeService(RecipeRepository recipeRepository, JDBCRecipeItemRepository recipeItemRepository, IngredientRepository ingredientRepository) {
        super(recipeRepository);
        this.ingredientRepository = ingredientRepository;
        this.recipeItemRepository = recipeItemRepository;
    }

    @Override
    @Transactional
    public Recipe create(RecipeCreationCommand command) {
        Recipe recipe = new Recipe();
        recipe.setName(command.name());
        recipe.setDescription(command.description());
        recipe.setCookingTime(command.cookingTime());

        Recipe newRecipe = recipeRepository.save(recipe);

        List<RecipeItem> recipeItems = command.recipeItems().stream()
                .map(recipeItemCreationCommand -> new RecipeItem(
                                null,
                                ingredientRepository.findById(recipeItemCreationCommand.ingredientId())
                                        .orElseThrow(IngredientNotFoundException::new),
                                newRecipe,
                                recipeItemCreationCommand.count(),
                                recipeItemCreationCommand.unit()
                        )
                ).toList();
        recipeItemRepository.batchSave(recipeItems);

        return recipe;
    }
}
