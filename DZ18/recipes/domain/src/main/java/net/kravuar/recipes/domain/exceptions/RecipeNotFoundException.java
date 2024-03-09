package net.kravuar.recipes.domain.exceptions;

public class RecipeNotFoundException extends BusinessException {

    public RecipeNotFoundException() {
        super("Recipe Not Found");
    }
}
