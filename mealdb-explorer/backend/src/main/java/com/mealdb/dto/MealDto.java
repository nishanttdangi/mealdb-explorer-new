package com.mealdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Simplified Meal DTO that flattens TheMealDB's flat ingredient/measure arrays
 * into a typed {@link IngredientDto} list for clean API responses.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MealDto {

    @JsonProperty("idMeal")
    private String id;

    @JsonProperty("strMeal")
    private String name;

    @JsonProperty("strCategory")
    private String category;

    @JsonProperty("strArea")
    private String area;

    @JsonProperty("strInstructions")
    private String instructions;

    @JsonProperty("strMealThumb")
    private String thumbnail;

    @JsonProperty("strYoutube")
    private String youtubeUrl;

    @JsonProperty("strSource")
    private String sourceUrl;

    @JsonProperty("strTags")
    private String tags;

    // Ingredients are composed after deserialization
    private List<IngredientDto> ingredients = new ArrayList<>();

    // Raw fields from TheMealDB (ingredient1..20 + measure1..20)
    @JsonProperty("strIngredient1")  private String ingredient1;
    @JsonProperty("strIngredient2")  private String ingredient2;
    @JsonProperty("strIngredient3")  private String ingredient3;
    @JsonProperty("strIngredient4")  private String ingredient4;
    @JsonProperty("strIngredient5")  private String ingredient5;
    @JsonProperty("strIngredient6")  private String ingredient6;
    @JsonProperty("strIngredient7")  private String ingredient7;
    @JsonProperty("strIngredient8")  private String ingredient8;
    @JsonProperty("strIngredient9")  private String ingredient9;
    @JsonProperty("strIngredient10") private String ingredient10;
    @JsonProperty("strIngredient11") private String ingredient11;
    @JsonProperty("strIngredient12") private String ingredient12;
    @JsonProperty("strIngredient13") private String ingredient13;
    @JsonProperty("strIngredient14") private String ingredient14;
    @JsonProperty("strIngredient15") private String ingredient15;
    @JsonProperty("strIngredient16") private String ingredient16;
    @JsonProperty("strIngredient17") private String ingredient17;
    @JsonProperty("strIngredient18") private String ingredient18;
    @JsonProperty("strIngredient19") private String ingredient19;
    @JsonProperty("strIngredient20") private String ingredient20;

    @JsonProperty("strMeasure1")  private String measure1;
    @JsonProperty("strMeasure2")  private String measure2;
    @JsonProperty("strMeasure3")  private String measure3;
    @JsonProperty("strMeasure4")  private String measure4;
    @JsonProperty("strMeasure5")  private String measure5;
    @JsonProperty("strMeasure6")  private String measure6;
    @JsonProperty("strMeasure7")  private String measure7;
    @JsonProperty("strMeasure8")  private String measure8;
    @JsonProperty("strMeasure9")  private String measure9;
    @JsonProperty("strMeasure10") private String measure10;
    @JsonProperty("strMeasure11") private String measure11;
    @JsonProperty("strMeasure12") private String measure12;
    @JsonProperty("strMeasure13") private String measure13;
    @JsonProperty("strMeasure14") private String measure14;
    @JsonProperty("strMeasure15") private String measure15;
    @JsonProperty("strMeasure16") private String measure16;
    @JsonProperty("strMeasure17") private String measure17;
    @JsonProperty("strMeasure18") private String measure18;
    @JsonProperty("strMeasure19") private String measure19;
    @JsonProperty("strMeasure20") private String measure20;

    /**
     * Builds the {@code ingredients} list from the flat strIngredientN / strMeasureN fields.
     * Call this after Jackson deserialization.
     */
    public void composeIngredients() {
        String[] rawIngredients = {
            ingredient1, ingredient2, ingredient3, ingredient4, ingredient5,
            ingredient6, ingredient7, ingredient8, ingredient9, ingredient10,
            ingredient11, ingredient12, ingredient13, ingredient14, ingredient15,
            ingredient16, ingredient17, ingredient18, ingredient19, ingredient20
        };
        String[] rawMeasures = {
            measure1, measure2, measure3, measure4, measure5,
            measure6, measure7, measure8, measure9, measure10,
            measure11, measure12, measure13, measure14, measure15,
            measure16, measure17, measure18, measure19, measure20
        };

        ingredients = new ArrayList<>();
        for (int i = 0; i < rawIngredients.length; i++) {
            String ing = rawIngredients[i];
            if (ing != null && !ing.isBlank()) {
                String measure = (rawMeasures[i] != null) ? rawMeasures[i].trim() : "";
                ingredients.add(new IngredientDto(ing.trim(), measure));
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class IngredientDto {
        private String name;
        private String measure;

        public IngredientDto(String name, String measure) {
            this.name = name;
            this.measure = measure;
        }
    }
}
