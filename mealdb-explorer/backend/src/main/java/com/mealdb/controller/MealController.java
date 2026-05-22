package com.mealdb.controller;

import com.mealdb.dto.ApiResponse;
import com.mealdb.dto.MealDto;
import com.mealdb.dto.MealSummaryDto;
import com.mealdb.service.MealDbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for meal search, detail, and random endpoints.
 */
@RestController
@RequestMapping("/api/v1/meals")
@RequiredArgsConstructor
@Tag(name = "Meals", description = "Search, browse, and inspect meal details")
public class MealController {

    private final MealDbService mealDbService;

    @GetMapping("/search")
    @Operation(summary = "Search meals by name")
    public ResponseEntity<ApiResponse<List<MealSummaryDto>>> search(
            @Parameter(description = "Meal name or partial name")
            @RequestParam(defaultValue = "") String q) {
        List<MealSummaryDto> results = mealDbService.searchByName(q);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/letter/{letter}")
    @Operation(summary = "Search meals by first letter")
    public ResponseEntity<ApiResponse<List<MealSummaryDto>>> byLetter(
            @PathVariable char letter) {
        List<MealSummaryDto> results = mealDbService.searchByFirstLetter(letter);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full meal details by ID")
    public ResponseEntity<ApiResponse<MealDto>> getById(@PathVariable String id) {
        MealDto meal = mealDbService.getMealById(id);
        if (meal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.ok(meal));
    }

    @GetMapping("/random")
    @Operation(summary = "Get a random meal")
    public ResponseEntity<ApiResponse<MealDto>> random() {
        MealDto meal = mealDbService.getRandomMeal();
        return ResponseEntity.ok(ApiResponse.ok(meal));
    }

    @GetMapping("/ingredient")
    @Operation(summary = "Filter meals by main ingredient")
    public ResponseEntity<ApiResponse<List<MealSummaryDto>>> byIngredient(
            @RequestParam String i) {
        List<MealSummaryDto> results = mealDbService.getMealsByIngredient(i);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }
}
