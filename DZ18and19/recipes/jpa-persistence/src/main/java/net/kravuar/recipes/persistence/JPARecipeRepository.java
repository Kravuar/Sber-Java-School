package net.kravuar.recipes.persistence;

import net.kravuar.recipes.domain.Recipe;
import net.kravuar.recipes.repositories.RecipeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPARecipeRepository extends JpaRepository<Recipe, Long>, RecipeRepository {
}
