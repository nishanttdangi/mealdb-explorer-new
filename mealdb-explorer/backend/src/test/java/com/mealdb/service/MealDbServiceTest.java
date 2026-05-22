package com.mealdb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealdb.dto.MealDto;
import com.mealdb.dto.MealSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MealDbServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MealDbService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseUrl", "https://www.themealdb.com/api/json/v1/1");
        ReflectionTestUtils.setField(service, "objectMapper", objectMapper);
    }

    @Test
    void searchByName_returnsResults() throws Exception {
        String json = """
            {
              "meals": [
                { "idMeal": "52772", "strMeal": "Teriyaki Chicken Casserole", "strMealThumb": "https://example.com/img.jpg" }
              ]
            }
            """;
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(json));

        List<MealSummaryDto> results = service.searchByName("teriyaki");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo("52772");
        assertThat(results.get(0).getName()).isEqualTo("Teriyaki Chicken Casserole");
    }

    @Test
    void searchByName_emptyResponse_returnsEmptyList() {
        String json = "{ \"meals\": null }";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(json));

        List<MealSummaryDto> results = service.searchByName("xyznotfound");

        assertThat(results).isEmpty();
    }

    @Test
    void getMealById_returnsMealWithIngredients() throws Exception {
        String json = """
            {
              "meals": [{
                "idMeal": "52772",
                "strMeal": "Teriyaki Chicken Casserole",
                "strCategory": "Chicken",
                "strArea": "Japanese",
                "strInstructions": "Mix and bake.",
                "strMealThumb": "https://example.com/img.jpg",
                "strYoutube": "https://youtube.com/watch?v=abc",
                "strIngredient1": "Soy Sauce",
                "strMeasure1": "3/4 cup"
              }]
            }
            """;
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(json));

        MealDto meal = service.getMealById("52772");

        assertThat(meal).isNotNull();
        assertThat(meal.getId()).isEqualTo("52772");
        assertThat(meal.getIngredients()).hasSize(1);
        assertThat(meal.getIngredients().get(0).getName()).isEqualTo("Soy Sauce");
    }
}
