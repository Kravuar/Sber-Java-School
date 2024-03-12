package net.kravuar.recipes.persistence;

import jakarta.transaction.Transactional;
import net.kravuar.recipes.domain.AppComponent;
import net.kravuar.recipes.domain.Recipe;
import net.kravuar.recipes.domain.commands.RecipeCreationCommand;
import net.kravuar.recipes.repositories.RecipeRepository;
import net.kravuar.recipes.services.DelegatingRecipeService;

@AppComponent
class JPARecipeService extends DelegatingRecipeService {

    public JPARecipeService(RecipeRepository recipeRepository, JPAIngredientRepository ingredientRepository) {
        super(recipeRepository);
    }

    @Override
    @Transactional
    public Recipe create(RecipeCreationCommand command) {
        Recipe recipe = new Recipe();
        recipe.setName(command.name());
        recipe.setDescription(command.description());
        recipe.setCookingTime(command.cookingTime());

        return recipeRepository.save(recipe);
    }
}
