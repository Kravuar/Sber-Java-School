package net.kravuar.recipes;

import net.kravuar.recipes.domain.RecipeItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface RecipeItemMapper {
    RecipeItemDTO toDto(RecipeItem recipeItem);
}
