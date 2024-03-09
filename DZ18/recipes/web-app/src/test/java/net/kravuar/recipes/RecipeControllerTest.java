package net.kravuar.recipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kravuar.recipes.domain.Ingredient;
import net.kravuar.recipes.domain.Recipe;
import net.kravuar.recipes.domain.RecipeItem;
import net.kravuar.recipes.domain.commands.RecipeCreationCommand;
import net.kravuar.recipes.domain.commands.RecipeItemCreationCommand;
import net.kravuar.recipes.domain.exceptions.RecipeNotFoundException;
import net.kravuar.recipes.repositories.IngredientRepository;
import net.kravuar.recipes.repositories.RecipeItemRepository;
import net.kravuar.recipes.repositories.RecipeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private RecipeItemRepository recipeItemRepository;

    private RecipeCreationCommand testRecipeCreationCommand() {
        List<RecipeItemCreationCommand> recipeItemDTOList = new ArrayList<>();
        RecipeItemCreationCommand recipeItemCreationCommand = new RecipeItemCreationCommand(
                1L,
                2.5,
                RecipeItem.CountUnit.GRAM
        );
        recipeItemDTOList.add(recipeItemCreationCommand);

        return new RecipeCreationCommand(
                "Test Recipe",
                Duration.ofMinutes(30),
                "Test Description",
                recipeItemDTOList
        );
    }


    private static Recipe testRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("Test Recipe");
        recipe.setCookingTime(Duration.ofMinutes(30));
        recipe.setDescription("Test Description");
        recipe.setRecipeItems(Collections.singletonList(testRecipeItem(recipe)));

        return recipe;
    }

    private static RecipeItem testRecipeItem(Recipe recipe) {
        RecipeItem recipeItem = new RecipeItem();
        recipeItem.setIngredient(testIngredient());
        recipeItem.setCount(2.5);
        recipeItem.setRecipe(recipe);
        recipeItem.setCountUnit(RecipeItem.CountUnit.GRAM);

        return recipeItem;
    }

    private static Ingredient testIngredient() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Test Ingredient");
        ingredient.setDescription("Test Description");

        return ingredient;
    }

    @BeforeAll
    void ingredientInit() {
        ingredientRepository.save(testIngredient());
    }

    @BeforeEach
    void init() {
        recipeItemRepository.deleteAll();
        recipeRepository.deleteAll();
    }

    @Test
    void givenNoExistingRecipe_whenSaveRecipe_thenRecipeIsCreatedWithId() throws Exception {
        // Given
        RecipeCreationCommand recipe = testRecipeCreationCommand();

        // When
        String response = mockMvc.perform(post("/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Then
        assertThat(objectMapper.readValue(response, RecipeItem.class)).isNotNull();
    }

    @Test
    void givenExistingRecipes_whenFindAll_thenReturnAllRecipes() throws Exception {
        // Given
        Recipe recipe = testRecipe();
        recipe = recipeRepository.save(recipe);
        recipeItemRepository.save(testRecipeItem(recipe));

        // When & Then
        mockMvc.perform(get("/recipe/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Recipe"));
    }

    @Test
    void givenExistingRecipe_whenFindRecipesByName_thenReturnMatchingRecipes() throws Exception {
        // Given
        Recipe recipe = testRecipe();
        recipe =  recipeRepository.save(recipe);
        recipeItemRepository.save(testRecipeItem(recipe));

        // When & Then
        mockMvc.perform(get("/recipe/name/Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Recipe"));
    }

    @Test
    void givenExistingRecipes_whenFindRecipesByCookingTime_thenReturnMatchingRecipes() throws Exception {
        // Given
        Recipe recipe = testRecipe();
        recipe =  recipeRepository.save(recipe);
        recipeItemRepository.save(testRecipeItem(recipe));

        // When & Then
        mockMvc.perform(get("/recipe/cooking-time/PT60M"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Recipe"));
    }

    @Test
    public void givenNoExistingRecipe_whenFindRecipeById_thenThrows() throws Exception {
        // Given
        long nonExistingRecipeId = -1L;

        // When & Then
        mockMvc.perform(get("/recipe/" + nonExistingRecipeId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof RecipeNotFoundException).isTrue());
    }

    @Test
    public void givenNoExistingRecipes_whenFindAll_thenReturnEmptyList() throws Exception {
        // When & Then
        mockMvc.perform(get("/recipe/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void givenNoExistingRecipes_whenFindRecipesByName_thenReturnEmptyList() throws Exception {
        // Given
        String nonExistingName = "NonExistingRecipe";

        // When & Then
        mockMvc.perform(get("/recipe/name/" + nonExistingName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}