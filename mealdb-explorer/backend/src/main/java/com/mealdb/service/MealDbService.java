package com.mealdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealdb.config.CacheConfig;
import com.mealdb.dto.CategoryDto;
import com.mealdb.dto.MealDto;
import com.mealdb.dto.MealSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service layer that wraps the TheMealDB API.
 *
 * <p>All methods cache their results using Caffeine (see {@link CacheConfig}).
 * Cache keys are derived from the method parameters.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MealDbService {

    @Value("${mealdb.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Search meals by name.
     *
     * @param name partial or full meal name
     * @return list of matching meal summaries
     */
    @Cacheable(value = CacheConfig.CACHE_MEALS, key = "#name.toLowerCase()")
    public List<MealSummaryDto> searchByName(String name) {
        log.debug("Cache MISS – searchByName({})", name);
        String url = baseUrl + "/search.php?s=" + name;
        return fetchMealSummaryList(url, "meals");
    }

    /**
     * Search meals by first letter.
     */
    @Cacheable(value = CacheConfig.CACHE_MEALS, key = "'letter_' + #letter")
    public List<MealSummaryDto> searchByFirstLetter(char letter) {
        log.debug("Cache MISS – searchByFirstLetter({})", letter);
        String url = baseUrl + "/search.php?f=" + letter;
        return fetchMealSummaryList(url, "meals");
    }

    // ── Detail ────────────────────────────────────────────────────────────────

    /**
     * Retrieve full meal details including ingredients and instructions.
     *
     * @param id TheMealDB meal ID
     */
    @Cacheable(value = CacheConfig.CACHE_MEAL_DETAIL, key = "#id")
    public MealDto getMealById(String id) {
        log.debug("Cache MISS – getMealById({})", id);
        String url = baseUrl + "/lookup.php?i=" + id;
        List<MealDto> meals = fetchMealDtoList(url, "meals");
        return meals.isEmpty() ? null : meals.get(0);
    }

    // ── Random ────────────────────────────────────────────────────────────────

    /**
     * Fetch a random meal. Not cached – always live.
     */
    public MealDto getRandomMeal() {
        log.debug("Fetching random meal (no cache)");
        String url = baseUrl + "/random.php";
        List<MealDto> meals = fetchMealDtoList(url, "meals");
        return meals.isEmpty() ? null : meals.get(0);
    }

    // ── Categories ────────────────────────────────────────────────────────────

    /**
     * List all meal categories.
     */
    @Cacheable(value = CacheConfig.CACHE_CATEGORIES)
    public List<CategoryDto> getAllCategories() {
        log.debug("Cache MISS – getAllCategories()");
        String url = baseUrl + "/categories.php";
        return fetchList(url, "categories", CategoryDto.class);
    }

    /**
     * List meals in a given category (summary only).
     */
    @Cacheable(value = CacheConfig.CACHE_BY_CATEGORY, key = "#category.toLowerCase()")
    public List<MealSummaryDto> getMealsByCategory(String category) {
        log.debug("Cache MISS – getMealsByCategory({})", category);
        String url = baseUrl + "/filter.php?c=" + category;
        return fetchMealSummaryList(url, "meals");
    }

    // ── Areas ─────────────────────────────────────────────────────────────────

    /**
     * List all cuisine areas.
     */
    @Cacheable(value = CacheConfig.CACHE_AREAS)
    public List<String> getAllAreas() {
        log.debug("Cache MISS – getAllAreas()");
        String url = baseUrl + "/list.php?a=list";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode mealsNode = root.get("meals");
            if (mealsNode == null || mealsNode.isNull()) return Collections.emptyList();
            List<String> areas = new ArrayList<>();
            for (JsonNode node : mealsNode) {
                JsonNode areaNode = node.get("strArea");
                if (areaNode != null) areas.add(areaNode.asText());
            }
            return areas;
        } catch (Exception e) {
            log.error("Error fetching areas: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * List meals by cuisine area.
     */
    @Cacheable(value = CacheConfig.CACHE_BY_AREA, key = "#area.toLowerCase()")
    public List<MealSummaryDto> getMealsByArea(String area) {
        log.debug("Cache MISS – getMealsByArea({})", area);
        String url = baseUrl + "/filter.php?a=" + area;
        return fetchMealSummaryList(url, "meals");
    }

    // ── Ingredients ───────────────────────────────────────────────────────────

    /**
     * List meals by main ingredient.
     */
    @Cacheable(value = CacheConfig.CACHE_INGREDIENTS, key = "#ingredient.toLowerCase()")
    public List<MealSummaryDto> getMealsByIngredient(String ingredient) {
        log.debug("Cache MISS – getMealsByIngredient({})", ingredient);
        String url = baseUrl + "/filter.php?i=" + ingredient;
        return fetchMealSummaryList(url, "meals");
    }

    // ── Scheduled cache eviction ──────────────────────────────────────────────

    /**
     * Evict category cache every 6 hours (categories rarely change).
     */
    @Scheduled(fixedDelay = 6 * 60 * 60 * 1000)
    @CacheEvict(value = CacheConfig.CACHE_CATEGORIES, allEntries = true)
    public void evictCategoryCache() {
        log.info("Scheduled eviction – categories cache cleared");
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private List<MealSummaryDto> fetchMealSummaryList(String url, String nodeKey) {
        return fetchList(url, nodeKey, MealSummaryDto.class);
    }

    private List<MealDto> fetchMealDtoList(String url, String nodeKey) {
        List<MealDto> meals = fetchList(url, nodeKey, MealDto.class);
        meals.forEach(MealDto::composeIngredients);
        return meals;
    }

    private <T> List<T> fetchList(String url, String nodeKey, Class<T> clazz) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode node = root.get(nodeKey);
            if (node == null || node.isNull()) return Collections.emptyList();

            List<T> result = new ArrayList<>();
            for (JsonNode item : node) {
                result.add(objectMapper.treeToValue(item, clazz));
            }
            return result;
        } catch (Exception e) {
            log.error("Error calling TheMealDB [{}]: {}", url, e.getMessage());
            return Collections.emptyList();
        }
    }
}
