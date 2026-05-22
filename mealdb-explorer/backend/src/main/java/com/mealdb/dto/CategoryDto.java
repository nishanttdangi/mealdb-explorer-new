package com.mealdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a meal category as returned by TheMealDB.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDto {

    @JsonProperty("idCategory")
    private String id;

    @JsonProperty("strCategory")
    private String name;

    @JsonProperty("strCategoryThumb")
    private String thumbnail;

    @JsonProperty("strCategoryDescription")
    private String description;
}
