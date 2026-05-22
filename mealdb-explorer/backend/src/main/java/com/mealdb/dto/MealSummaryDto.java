package com.mealdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight meal summary used for search results and category listings.
 * Does NOT include ingredients or full instructions.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MealSummaryDto {

    @JsonProperty("idMeal")
    private String id;

    @JsonProperty("strMeal")
    private String name;

    @JsonProperty("strMealThumb")
    private String thumbnail;

    @JsonProperty("strCategory")
    private String category;

    @JsonProperty("strArea")
    private String area;
}
