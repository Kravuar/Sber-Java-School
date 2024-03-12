package net.kravuar.recipes.persistence;

import net.kravuar.recipes.domain.RecipeItem;
import net.kravuar.recipes.repositories.RecipeItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPARecipeItemRepository extends JpaRepository<RecipeItem, Long>, RecipeItemRepository {
}
