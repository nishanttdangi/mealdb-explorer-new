package com.mealdb.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * In-memory cache configuration using Caffeine.
 *
 * <p>Each named cache has:
 * <ul>
 *   <li>TTL  – 30 minutes (expireAfterWrite)</li>
 *   <li>Max  – 500 entries</li>
 * </ul>
 *
 * <p>Named caches:
 * <ul>
 *   <li>{@code meals}      – search results</li>
 *   <li>{@code mealDetail} – individual meal detail</li>
 *   <li>{@code categories} – category list</li>
 *   <li>{@code byCategory} – meals filtered by category</li>
 *   <li>{@code random}     – random meal (shorter TTL via separate spec)</li>
 * </ul>
 */
@Configuration
public class CacheConfig {

    public static final String CACHE_MEALS        = "meals";
    public static final String CACHE_MEAL_DETAIL  = "mealDetail";
    public static final String CACHE_CATEGORIES   = "categories";
    public static final String CACHE_BY_CATEGORY  = "byCategory";
    public static final String CACHE_RANDOM       = "random";
    public static final String CACHE_INGREDIENTS  = "ingredients";
    public static final String CACHE_BY_AREA      = "byArea";
    public static final String CACHE_AREAS        = "areas";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();

        // Standard caches – 30-min TTL, max 500 entries
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(500)
                        .recordStats()
        );

        manager.setCacheNames(List.of(
                CACHE_MEALS,
                CACHE_MEAL_DETAIL,
                CACHE_CATEGORIES,
                CACHE_BY_CATEGORY,
                CACHE_INGREDIENTS,
                CACHE_BY_AREA,
                CACHE_AREAS,
                CACHE_RANDOM
        ));

        return manager;
    }
}
