/**
 * api.js — HTTP client for the Spring Boot backend.
 * All methods return parsed { success, data } or throw on network error.
 */

const BASE_URL = 'http://localhost:8080/api/v1';

const Api = (() => {
  async function _get(path) {
    const res = await fetch(`${BASE_URL}${path}`);
    if (!res.ok) throw new Error(`HTTP ${res.status} – ${path}`);
    return res.json();
  }

  async function _post(path, body) {
    const res = await fetch(`${BASE_URL}${path}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });
    if (!res.ok) throw new Error(`HTTP ${res.status} – ${path}`);
    return res.json();
  }

  async function _delete(path) {
    const res = await fetch(`${BASE_URL}${path}`, { method: 'DELETE' });
    if (!res.ok) throw new Error(`HTTP ${res.status} – ${path}`);
    return res.json();
  }

  return {
    /** Search meals by name */
    searchMeals: (q) => _get(`/meals/search?q=${encodeURIComponent(q)}`),

    /** Get full meal detail */
    getMealById: (id) => _get(`/meals/${id}`),

    /** Get random meal */
    getRandomMeal: () => _get('/meals/random'),

    /** Get all categories */
    getCategories: () => _get('/categories'),

    /** Get meals in a category */
    getMealsByCategory: (cat) => _get(`/categories/${encodeURIComponent(cat)}/meals`),

    /** Get all areas */
    getAreas: () => _get('/areas'),

    /** Get meals by area */
    getMealsByArea: (area) => _get(`/areas/${encodeURIComponent(area)}/meals`),

    /** Filter by ingredient */
    getMealsByIngredient: (ing) => _get(`/meals/ingredient?i=${encodeURIComponent(ing)}`),

    /** Favourites CRUD */
    getFavourites: () => _get('/favourites'),
    isFavourite: (mealId) => _get(`/favourites/${mealId}/status`),
    addFavourite: (meal) => _post('/favourites', meal),
    removeFavourite: (mealId) => _delete(`/favourites/${mealId}`),
  };
})();
