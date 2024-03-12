package net.kravuar.recipes.persistence;

import net.kravuar.recipes.domain.Ingredient;
import net.kravuar.recipes.repositories.IngredientRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAIngredientRepository extends JpaRepository<Ingredient, Long>, IngredientRepository {
}
