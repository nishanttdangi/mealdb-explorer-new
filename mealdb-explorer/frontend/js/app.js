/**
 * app.js — Application controller for TheMealDB Explorer.
 * Wires together API calls and UI rendering.
 */

/* ── State ──────────────────────────────────────────────────────── */
let currentMeal = null;
let currentMealIsFav = false;

/* ── Initialise ─────────────────────────────────────────────────── */
window.addEventListener('DOMContentLoaded', async () => {
  await loadCategories();

  // Wire up search on Enter
  document.getElementById('searchInput').addEventListener('keydown', (e) => {
    if (e.key === 'Enter') handleSearch();
  });
});

/* ── Home / Categories ──────────────────────────────────────────── */
async function loadCategories() {
  try {
    const res = await Api.getCategories();
    if (res.success && res.data.length) {
      renderCategories(res.data);
    }
  } catch (err) {
    console.error('Failed to load categories:', err);
    showToast('⚠️ Could not reach the server. Is Spring Boot running?');
  }
}

function showHome() {
  showSection('categoriesSection');
  setNavActive(document.querySelector('.nav__link:first-child'));
  document.getElementById('searchInput').value = '';
}

/* ── Search ─────────────────────────────────────────────────────── */
async function handleSearch() {
  const q = document.getElementById('searchInput').value.trim();
  if (!q) {
    showToast('Please enter a meal name to search.');
    return;
  }

  setResultsTitle(`Results for "${q}"`, null);
  showSection('resultsSection');
  document.getElementById('mealsGrid').innerHTML = loadingHTML();

  try {
    const res = await Api.searchMeals(q);
    if (res.success) {
      setResultsTitle(`Results for "${q}"`, res.data.length);
      renderMealCards(res.data);
    }
  } catch (err) {
    console.error(err);
    showToast('Search failed. Check the server is running.');
    renderMealCards([]);
  }
}

/* ── Category Click ─────────────────────────────────────────────── */
async function handleCategoryClick(categoryName) {
  setResultsTitle(categoryName, null);
  showSection('resultsSection');
  document.getElementById('mealsGrid').innerHTML = loadingHTML();

  try {
    const res = await Api.getMealsByCategory(categoryName);
    if (res.success) {
      setResultsTitle(categoryName, res.data.length);
      renderMealCards(res.data);
    }
  } catch (err) {
    console.error(err);
    renderMealCards([]);
  }
}

/* ── Random ─────────────────────────────────────────────────────── */
async function handleRandom() {
  const btn = document.querySelector('.btn--hungry');
  const origHTML = btn.innerHTML;
  btn.innerHTML = `<span class="spinner"></span> Finding…`;
  btn.disabled = true;

  try {
    const res = await Api.getRandomMeal();
    if (res.success && res.data) {
      await _showMealModal(res.data);
    }
  } catch (err) {
    console.error(err);
    showToast('Could not fetch a random meal. Is the server running?');
  } finally {
    btn.innerHTML = origHTML;
    btn.disabled = false;
  }
}

/* ── Meal Modal ─────────────────────────────────────────────────── */
async function openMealModal(mealId) {
  openModal();

  // Show loading state in modal
  document.getElementById('modalTitle').textContent = 'Loading…';
  document.getElementById('ingredientsList').innerHTML = '';
  document.getElementById('instructionText').textContent = '';

  try {
    const [mealRes, favRes] = await Promise.all([
      Api.getMealById(mealId),
      Api.isFavourite(mealId),
    ]);

    if (mealRes.success && mealRes.data) {
      currentMeal = mealRes.data;
      currentMealIsFav = favRes.success ? favRes.data : false;
      populateModal(currentMeal, currentMealIsFav);
    }
  } catch (err) {
    console.error(err);
    document.getElementById('modalTitle').textContent = 'Failed to load';
  }
}

async function _showMealModal(meal) {
  currentMeal = meal;
  openModal();
  try {
    const favRes = await Api.isFavourite(meal.id);
    currentMealIsFav = favRes.success ? favRes.data : false;
  } catch {
    currentMealIsFav = false;
  }
  populateModal(currentMeal, currentMealIsFav);
}

/* ── Favourites ─────────────────────────────────────────────────── */
async function toggleFavourite() {
  if (!currentMeal) return;
  const btn = document.getElementById('modalFavBtn');

  try {
    if (currentMealIsFav) {
      await Api.removeFavourite(currentMeal.id);
      currentMealIsFav = false;
      showToast('Removed from favourites');
    } else {
      await Api.addFavourite({
        id:        currentMeal.id,
        name:      currentMeal.name,
        thumbnail: currentMeal.thumbnail,
        category:  currentMeal.category,
      });
      currentMealIsFav = true;
      showToast('❤️ Added to favourites!');
    }
    updateFavBtn(btn, currentMealIsFav);
  } catch (err) {
    console.error(err);
    showToast('Action failed. Try again.');
  }
}

async function showFavourites() {
  showSection('favouritesSection');
  setNavActive(document.querySelector('.nav__link:last-child'));
  document.getElementById('favsGrid').innerHTML = loadingHTML();

  try {
    const res = await Api.getFavourites();
    document.getElementById('favsCount').textContent =
      res.data ? `${res.data.length} saved recipe${res.data.length !== 1 ? 's' : ''}` : '';
    renderFavouriteCards(res.data || []);
  } catch (err) {
    console.error(err);
    renderFavouriteCards([]);
  }
}

/* ── Helpers ────────────────────────────────────────────────────── */
function loadingHTML() {
  return `
    <div class="skeleton-grid" style="grid-column:1/-1; display:grid; grid-template-columns: repeat(auto-fill,minmax(240px,1fr)); gap:1.25rem">
      ${Array(8).fill('<div class="skeleton" style="aspect-ratio:4/3;border-radius:14px"></div>').join('')}
    </div>`;
}
