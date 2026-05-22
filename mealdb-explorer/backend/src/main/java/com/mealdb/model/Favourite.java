package com.mealdb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Persists a user's favourite meal to the database.
 * Stored in table {@code favourites}.
 */
@Entity
@Table(name = "favourites", uniqueConstraints = @UniqueConstraint(columnNames = "meal_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meal_id", nullable = false, unique = true)
    private String mealId;

    @Column(name = "meal_name", nullable = false)
    private String mealName;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "category")
    private String category;

    @CreationTimestamp
    @Column(name = "added_at", updatable = false)
    private Instant addedAt;
}
