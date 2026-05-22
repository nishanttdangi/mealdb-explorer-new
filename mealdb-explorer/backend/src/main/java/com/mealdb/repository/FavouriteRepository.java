package com.mealdb.repository;

import com.mealdb.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Favourite} entities.
 */
@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    Optional<Favourite> findByMealId(String mealId);

    boolean existsByMealId(String mealId);

    void deleteByMealId(String mealId);
}
