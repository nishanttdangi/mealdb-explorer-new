-- ================================================================
--  TheMealDB Explorer — MySQL Schema
--  Run this script if you prefer manual table creation over
--  Hibernate's ddl-auto=update
-- ================================================================

CREATE DATABASE IF NOT EXISTS mealdb_explorer
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE mealdb_explorer;

CREATE TABLE IF NOT EXISTS favourites (
  id        BIGINT        NOT NULL AUTO_INCREMENT,
  meal_id   VARCHAR(20)   NOT NULL,
  meal_name VARCHAR(255)  NOT NULL,
  thumbnail VARCHAR(500)  NULL,
  category  VARCHAR(100)  NULL,
  added_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_meal_id (meal_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
