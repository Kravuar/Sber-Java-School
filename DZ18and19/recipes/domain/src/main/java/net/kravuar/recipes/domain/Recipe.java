package net.kravuar.recipes.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Recipe {
    private Long id;
    private String name;
    private Duration cookingTime;
    private String description;
    private List<RecipeItem> recipeItems;
}
