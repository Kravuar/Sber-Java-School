package net.kravuar.recipes;

import net.kravuar.recipes.domain.Recipe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecipeItemMapper.class})
interface RecipeMapper {
    RecipeDTO toDto(Recipe recipe);
}
