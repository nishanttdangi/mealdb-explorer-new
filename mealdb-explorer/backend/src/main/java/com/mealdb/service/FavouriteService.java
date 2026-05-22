package com.mealdb.service;

import com.mealdb.dto.MealSummaryDto;
import com.mealdb.model.Favourite;
import com.mealdb.repository.FavouriteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing user favourite meals persisted in the database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavouriteService {

    private final FavouriteRepository repository;

    public List<Favourite> getAll() {
        return repository.findAll();
    }

    public boolean isFavourite(String mealId) {
        return repository.existsByMealId(mealId);
    }

    @Transactional
    public Favourite addFavourite(MealSummaryDto meal) {
        if (repository.existsByMealId(meal.getId())) {
            return repository.findByMealId(meal.getId()).orElseThrow();
        }
        Favourite fav = new Favourite();
        fav.setMealId(meal.getId());
        fav.setMealName(meal.getName());
        fav.setThumbnail(meal.getThumbnail());
        fav.setCategory(meal.getCategory());
        return repository.save(fav);
    }

    @Transactional
    public void removeFavourite(String mealId) {
        repository.deleteByMealId(mealId);
        log.info("Removed favourite mealId={}", mealId);
    }
}
