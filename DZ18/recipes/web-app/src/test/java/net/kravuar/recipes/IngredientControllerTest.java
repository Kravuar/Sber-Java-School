package net.kravuar.recipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kravuar.recipes.domain.Ingredient;
import net.kravuar.recipes.domain.exceptions.IngredientNotFoundException;
import net.kravuar.recipes.repositories.IngredientRepository;
import net.kravuar.recipes.repositories.RecipeItemRepository;
import net.kravuar.recipes.repositories.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IngredientControllerTest {
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

    private static Ingredient testIngredient() {
        return someIngredient("Test Ingredient");
    }

    private static Ingredient someIngredient(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setDescription("Test Description");

        return ingredient;
    }

    @BeforeEach
    void init() {
        recipeItemRepository.deleteAll();
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Test
    void givenNoExistingIngredient_whenSaveIngredient_thenIngredientIsCreatedWithId() throws Exception {
        // Given
        Ingredient ingredient = testIngredient();

        // When
        String response = mockMvc.perform(post("/ingredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredient)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Then
        assertThat(objectMapper.readValue(response, Ingredient.class)).isNotNull();
    }

    @Test
    void givenExistingIngredient_whenFindIngredientById_thenReturnIngredient() throws Exception {
        // Given
        Ingredient ingredient = ingredientRepository.save(testIngredient());

        // When & Then
        mockMvc.perform(get("/ingredient/" + ingredient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ingredient.getId()))
                .andExpect(jsonPath("$.name").value("Test Ingredient"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void givenExistingIngredients_whenFindAll_thenReturnAllIngredients() throws Exception {
        // Given
        ingredientRepository.save(someIngredient("Test 1"));
        ingredientRepository.save(someIngredient("Test 2"));

        // When & Then
        mockMvc.perform(get("/ingredient/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Test 1"))
                .andExpect(jsonPath("$[1].name").value("Test 2"));
    }

    @Test
    void givenExistingIngredient_whenFindIngredientsByName_thenReturnMatchingIngredients() throws Exception {
        // Given
        ingredientRepository.save(testIngredient());

        // When & Then
        mockMvc.perform(get("/ingredient/name/Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Ingredient"));
    }

    @Test
    public void givenNoExistingIngredient_whenFindIngredientById_thenThrows() throws Exception {
        // Given
        long nonExistingIngredientId = -1L;

        // When & Then
        mockMvc.perform(get("/ingredient/" + nonExistingIngredientId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof IngredientNotFoundException).isTrue());
    }

    @Test
    public void givenNoExistingIngredients_whenFindAll_thenReturnEmptyList() throws Exception {
        // When & Then
        mockMvc.perform(get("/ingredient/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void givenNoExistingIngredients_whenFindIngredientsByName_thenReturnEmptyList() throws Exception {
        // Given
        String nonExistingName = "NonExistingIngredient";

        // When & Then
        mockMvc.perform(get("/ingredient/name/" + nonExistingName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
