package com.mealdb.controller;

import com.mealdb.dto.ApiResponse;
import com.mealdb.dto.MealSummaryDto;
import com.mealdb.service.MealDbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for browsing meals by cuisine area/region.
 */
@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
@Tag(name = "Areas", description = "Browse meals by cuisine region")
public class AreaController {

    private final MealDbService mealDbService;

    @GetMapping
    @Operation(summary = "List all cuisine areas")
    public ResponseEntity<ApiResponse<List<String>>> listAreas() {
        return ResponseEntity.ok(ApiResponse.ok(mealDbService.getAllAreas()));
    }

    @GetMapping("/{area}/meals")
    @Operation(summary = "Get meals by cuisine area")
    public ResponseEntity<ApiResponse<List<MealSummaryDto>>> mealsByArea(
            @PathVariable String area) {
        List<MealSummaryDto> meals = mealDbService.getMealsByArea(area);
        return ResponseEntity.ok(ApiResponse.ok(meals));
    }
}
