package com.mealdb.controller;

import com.mealdb.dto.ApiResponse;
import com.mealdb.dto.MealSummaryDto;
import com.mealdb.model.Favourite;
import com.mealdb.service.FavouriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user favourite meals.
 * Favourites are persisted in the configured database (H2 / MySQL).
 */
@RestController
@RequestMapping("/api/v1/favourites")
@RequiredArgsConstructor
@Tag(name = "Favourites", description = "Save and manage favourite meals")
public class FavouriteController {

    private final FavouriteService favouriteService;

    @GetMapping
    @Operation(summary = "Get all saved favourites")
    public ResponseEntity<ApiResponse<List<Favourite>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(favouriteService.getAll()));
    }

    @GetMapping("/{mealId}/status")
    @Operation(summary = "Check if a meal is favourited")
    public ResponseEntity<ApiResponse<Boolean>> isFavourite(@PathVariable String mealId) {
        return ResponseEntity.ok(ApiResponse.ok(favouriteService.isFavourite(mealId)));
    }

    @PostMapping
    @Operation(summary = "Add a meal to favourites")
    public ResponseEntity<ApiResponse<Favourite>> addFavourite(
            @RequestBody MealSummaryDto meal) {
        Favourite fav = favouriteService.addFavourite(meal);
        return ResponseEntity.ok(ApiResponse.ok("Added to favourites", fav));
    }

    @DeleteMapping("/{mealId}")
    @Operation(summary = "Remove a meal from favourites")
    public ResponseEntity<ApiResponse<Void>> removeFavourite(@PathVariable String mealId) {
        favouriteService.removeFavourite(mealId);
        return ResponseEntity.ok(ApiResponse.ok("Removed from favourites", null));
    }
}
