package net.kravuar.recipes.persistence;

import net.kravuar.recipes.domain.AppComponent;
import net.kravuar.recipes.domain.Ingredient;
import net.kravuar.recipes.repositories.IngredientRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AppComponent
class JDBCIngredientRepository implements IngredientRepository {
    public static final String TABLE = "ingredient";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    private final JdbcTemplate jdbcTemplate;
    private final IngredientRowMapper rowMapper;
    private final SimpleJdbcInsert insert;

    public JDBCIngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new IngredientRowMapper();
        this.insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE)
                .usingGeneratedKeyColumns(ID);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.execute("DELETE FROM " + TABLE);
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        long id = insert.executeAndReturnKey(
                new MapSqlParameterSource(Map.of(
                        NAME, ingredient.getName(),
                        DESCRIPTION, ingredient.getDescription()
                ))
        ).longValue();

        ingredient.setId(id);
        return ingredient;
    }

    @Override
    public Optional<Ingredient> findById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    String.format("SELECT * FROM %s WHERE %s = ?", TABLE, ID),
                    rowMapper,
                    id
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Ingredient> findAll() {
        return jdbcTemplate.query(
                String.format("SELECT * FROM %s", TABLE),
                rowMapper
        );
    }

    @Override
    public List<Ingredient> findByNameStartsWith(String name) {
        return jdbcTemplate.query(
                String.format("SELECT * FROM %s WHERE %s LIKE ?", TABLE, NAME),
                rowMapper,
                "%" + name + "%"
        );
    }

    private static class IngredientRowMapper implements RowMapper<Ingredient> {
        @Override
        public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getLong(ID));
            ingredient.setName(rs.getString(NAME));
            ingredient.setDescription(rs.getString(DESCRIPTION));
            return ingredient;
        }
    }
}
