package com.mealdb.controller;

import com.mealdb.dto.ApiResponse;
import com.mealdb.dto.CategoryDto;
import com.mealdb.dto.MealSummaryDto;
import com.mealdb.service.MealDbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for browsing meal categories and filtering meals by category or area.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Browse meal categories and areas")
public class CategoryController {

    private final MealDbService mealDbService;

    @GetMapping
    @Operation(summary = "List all meal categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(mealDbService.getAllCategories()));
    }

    @GetMapping("/{category}/meals")
    @Operation(summary = "Get meals by category name")
    public ResponseEntity<ApiResponse<List<MealSummaryDto>>> mealsByCategory(
            @PathVariable String category) {
        List<MealSummaryDto> meals = mealDbService.getMealsByCategory(category);
        return ResponseEntity.ok(ApiResponse.ok(meals));
    }
}
