package net.kravuar.recipes.domain.exceptions;

public class IngredientNotFoundException extends BusinessException {

    public IngredientNotFoundException() {
        super("Ingredient Not Found");
    }
}
