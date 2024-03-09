package net.kravuar.recipes.persistence;

import lombok.RequiredArgsConstructor;
import net.kravuar.recipes.domain.AppComponent;
import net.kravuar.recipes.domain.Recipe;
import net.kravuar.recipes.domain.RecipeItem;
import net.kravuar.recipes.repositories.RecipeRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AppComponent
class JDBCRecipeRepository implements RecipeRepository {
    private static final String TABLE = "recipe";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String COOKING_TIME = "cooking_time";

    private final JdbcTemplate jdbcTemplate;
    private final RecipeRowMapper rowMapper;
    private final SimpleJdbcInsert insert;

    public JDBCRecipeRepository(JdbcTemplate jdbcTemplate, JDBCRecipeItemRepository recipeItemRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new RecipeRowMapper(recipeItemRepository);
        this.insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE)
                .usingGeneratedKeyColumns(ID);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.execute("DELETE FROM " + TABLE);
    }

    @Override
    public Recipe save(Recipe recipe) {
        long id = insert.executeAndReturnKey(
                new MapSqlParameterSource(Map.of(
                        NAME, recipe.getName(),
                        DESCRIPTION, recipe.getDescription(),
                        COOKING_TIME, recipe.getCookingTime().toSeconds()
                ))
        ).longValue();

        recipe.setId(id);
        return recipe;
    }

    @Override
    public Optional<Recipe> findById(long recipeId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    String.format("SELECT * FROM %s WHERE %s=?", TABLE, ID),
                    rowMapper,
                    recipeId
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Recipe> findByNameStartsWith(String name) {
        return jdbcTemplate.query(
                String.format("SELECT * FROM %s WHERE %s LIKE ?", TABLE, NAME),
                rowMapper,
                "%" + name + "%"
        );
    }

    @Override
    public List<Recipe> findAll() {
        return jdbcTemplate.query(
                String.format("SELECT * FROM %s", TABLE),
                rowMapper
        );
    }

    @Override
    public List<Recipe> findByCookingTimeLessThan(Duration upperBound) {
        return jdbcTemplate.query(
                String.format("SELECT * FROM %s WHERE %s < ? ORDER BY %s DESC", TABLE, COOKING_TIME, COOKING_TIME),
                rowMapper,
                upperBound.toSeconds()
        );
    }

    @RequiredArgsConstructor
    private static class RecipeRowMapper implements RowMapper<Recipe> {
        private final JDBCRecipeItemRepository recipeItemRepository;

        @Override
        public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
            Recipe recipe = new Recipe();
            recipe.setId(rs.getLong(ID));
            recipe.setName(rs.getString(NAME));
            recipe.setCookingTime(Duration.ofSeconds(rs.getLong(COOKING_TIME)));
            recipe.setDescription(rs.getString(DESCRIPTION));

            // This is bad, additional queries
            List<RecipeItem> items = recipeItemRepository.findByRecipeId(recipe.getId());
            recipe.setRecipeItems(items);
            return recipe;
        }
    }
}
