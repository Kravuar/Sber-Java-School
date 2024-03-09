package net.kravuar.recipes.persistence;

import lombok.RequiredArgsConstructor;
import net.kravuar.recipes.domain.AppComponent;
import net.kravuar.recipes.domain.Ingredient;
import net.kravuar.recipes.domain.RecipeItem;
import net.kravuar.recipes.domain.exceptions.IngredientNotFoundException;
import net.kravuar.recipes.repositories.RecipeItemRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.lang.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AppComponent
class JDBCRecipeItemRepository implements RecipeItemRepository {
    public static final String TABLE = "recipe_item";
    public static final String ID = "id";
    public static final String RECIPE_ID = "recipe_id";
    public static final String INGREDIENT_ID = "ingredient_id";
    public static final String COUNT = "count";
    public static final String COUNT_UNIT = "count_unit";

    private final JdbcTemplate jdbcTemplate;
    private final RecipeItemRowMapper rowMapper;
    private final SimpleJdbcInsert insert;

    public JDBCRecipeItemRepository(JdbcTemplate jdbcTemplate, JDBCIngredientRepository ingredientRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new RecipeItemRowMapper(ingredientRepository);
        this.insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE)
                .usingGeneratedKeyColumns(ID);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.execute("DELETE FROM " + TABLE);
    }

    @Override
    public RecipeItem save(RecipeItem recipeItem) {
        long id = insert.executeAndReturnKey(
                new MapSqlParameterSource(Map.of(
                        RECIPE_ID, recipeItem.getRecipe().getId(),
                        INGREDIENT_ID, recipeItem.getIngredient().getId(),
                        COUNT, recipeItem.getCount(),
                        COUNT_UNIT, recipeItem.getCountUnit()
                ))
        ).longValue();

        recipeItem.setId(id);
        return recipeItem;
    }

    public void batchSave(List<RecipeItem> recipeItems) {
        jdbcTemplate.batchUpdate(
                String.format(
                        "INSERT INTO %s (%s, %s, %s, %s) VALUES (?,?,?,?)",
                        TABLE,
                        RECIPE_ID,
                        INGREDIENT_ID,
                        COUNT,
                        COUNT_UNIT
                ),
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                        RecipeItem item = recipeItems.get(i);
                        ps.setLong(1, item.getRecipe().getId());
                        ps.setLong(2, item.getIngredient().getId());
                        ps.setDouble(3, item.getCount());
                        ps.setString(4, item.getCountUnit().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return recipeItems.size();
                    }
                }
        );
    }

    @Override
    public Optional<RecipeItem> findById(long recipeItemId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    String.format("SELECT * FROM %s on WHERE %s=?", TABLE, ID),
                    rowMapper,
                    recipeItemId
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<RecipeItem> findByRecipeId(long recipeId) {
        return jdbcTemplate.query(
                String.format("SELECT * FROM %s WHERE %s=?", TABLE, RECIPE_ID),
                rowMapper,
                recipeId
        );
    }

    @RequiredArgsConstructor
    private static class RecipeItemRowMapper implements RowMapper<RecipeItem> {
        private final JDBCIngredientRepository ingredientRepository;

        @Override
        public RecipeItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            RecipeItem recipeItem = new RecipeItem();
            recipeItem.setCount(rs.getLong(COUNT));
            recipeItem.setCountUnit(RecipeItem.CountUnit.valueOf(rs.getString(COUNT_UNIT)));
            recipeItem.setId(rs.getLong(ID));

            // This is bad, additional queries
            Ingredient ingredient = ingredientRepository.findById(rs.getLong(INGREDIENT_ID))
                    .orElseThrow(IngredientNotFoundException::new);
            recipeItem.setIngredient(ingredient);
            return recipeItem;
        }
    }
}
