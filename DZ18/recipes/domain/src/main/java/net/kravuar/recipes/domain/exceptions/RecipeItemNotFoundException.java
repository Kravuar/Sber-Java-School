package net.kravuar.recipes.domain.exceptions;

public class RecipeItemNotFoundException extends BusinessException {

    public RecipeItemNotFoundException() {
        super("Recipe item Not Found");
    }
}
