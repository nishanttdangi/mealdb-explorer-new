/**
 * ui.js — DOM rendering helpers for TheMealDB Explorer.
 */

/* ── Toast ─────────────────────────────────────────────────────── */
let toastTimer;
function showToast(msg) {
  const t = document.getElementById('toast');
  t.textContent = msg;
  t.classList.add('is-visible');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => t.classList.remove('is-visible'), 2800);
}

/* ── Category Cards ─────────────────────────────────────────────── */
function renderCategories(categories) {
  const grid = document.getElementById('categoriesGrid');
  grid.innerHTML = '';
  categories.forEach((cat, i) => {
    const card = document.createElement('div');
    card.className = 'category-card';
    card.style.animationDelay = `${i * 0.05}s`;
    card.innerHTML = `
      <img class="category-card__img" src="${cat.thumbnail}" alt="${cat.name}" loading="lazy" />
      <span class="category-card__name">${cat.name}</span>
    `;
    card.addEventListener('click', () => handleCategoryClick(cat.name));
    grid.appendChild(card);
  });
}

/* ── Meal Cards ─────────────────────────────────────────────────── */
function renderMealCards(meals, containerId = 'mealsGrid') {
  const grid = document.getElementById(containerId);
  grid.innerHTML = '';

  if (!meals || meals.length === 0) {
    grid.innerHTML = `
      <div class="empty-state" style="grid-column:1/-1">
        <span class="empty-state__icon">🍽</span>
        <p class="empty-state__title">No meals found</p>
        <p class="empty-state__sub">Try a different search or browse by category</p>
      </div>`;
    return;
  }

  meals.forEach((meal, i) => {
    const card = document.createElement('div');
    card.className = 'meal-card';
    card.style.animationDelay = `${i * 0.04}s`;
    const cat  = meal.category ? `<span class="pill pill--accent">${meal.category}</span>` : '';
    const area = meal.area    ? `<span class="pill">${meal.area}</span>` : '';
    card.innerHTML = `
      <div class="meal-card__img-wrap">
        <img class="meal-card__img" src="${meal.thumbnail}" alt="${meal.name}" loading="lazy" />
        <div class="meal-card__overlay"></div>
      </div>
      <div class="meal-card__body">
        <p class="meal-card__name">${meal.name}</p>
        <div class="meal-card__meta">${cat}${area}</div>
      </div>`;
    card.addEventListener('click', () => openMealModal(meal.id));
    grid.appendChild(card);
  });
}

/* ── Favourites as Meal Cards ──────────────────────────────────── */
function renderFavouriteCards(favs) {
  const grid = document.getElementById('favsGrid');
  grid.innerHTML = '';

  if (!favs || favs.length === 0) {
    grid.innerHTML = `
      <div class="empty-state" style="grid-column:1/-1">
        <span class="empty-state__icon">❤️</span>
        <p class="empty-state__title">No favourites yet</p>
        <p class="empty-state__sub">Open any recipe and tap the heart button to save it here</p>
      </div>`;
    return;
  }

  // Map to MealSummaryDto shape for renderMealCards
  const mapped = favs.map(f => ({
    id:        f.mealId,
    name:      f.mealName,
    thumbnail: f.thumbnail,
    category:  f.category,
    area:      null,
  }));
  renderMealCards(mapped, 'favsGrid');
}

/* ── Modal ──────────────────────────────────────────────────────── */
function openModal() {
  document.getElementById('modalOverlay').classList.add('is-open');
  document.body.style.overflow = 'hidden';
}

function closeModal(event) {
  if (event && event.target !== document.getElementById('modalOverlay')) return;
  _doCloseModal();
}

function _doCloseModal() {
  document.getElementById('modalOverlay').classList.remove('is-open');
  document.body.style.overflow = '';
  // Stop YouTube
  const frame = document.getElementById('videoFrame');
  frame.src = '';
}

// Close on Escape
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') _doCloseModal();
});

/* ── Populate modal with meal detail ────────────────────────────── */
function populateModal(meal, isFav) {
  document.getElementById('modalThumb').src    = meal.thumbnail || '';
  document.getElementById('modalThumb').alt    = meal.name;
  document.getElementById('modalTitle').textContent = meal.name;

  // Badges
  const badges = document.getElementById('modalBadges');
  badges.innerHTML = [
    meal.category && `<span class="pill pill--accent">${meal.category}</span>`,
    meal.area     && `<span class="pill">${meal.area}</span>`,
  ].filter(Boolean).join('');

  // YouTube link & embed
  const ytBtn  = document.getElementById('modalYt');
  const videoW = document.getElementById('videoWrap');
  const frame  = document.getElementById('videoFrame');

  if (meal.youtubeUrl) {
    const videoId = extractYtId(meal.youtubeUrl);
    ytBtn.href = meal.youtubeUrl;
    ytBtn.style.display = 'flex';
    if (videoId) {
      frame.src = `https://www.youtube.com/embed/${videoId}`;
      videoW.style.display = 'block';
    } else {
      videoW.style.display = 'none';
    }
  } else {
    ytBtn.style.display = 'none';
    videoW.style.display = 'none';
  }

  // Fav button
  const favBtn = document.getElementById('modalFavBtn');
  updateFavBtn(favBtn, isFav);

  // Ingredients
  const ul = document.getElementById('ingredientsList');
  ul.innerHTML = '';
  (meal.ingredients || []).forEach(ing => {
    const li = document.createElement('li');
    li.className = 'ingredient-item';
    li.innerHTML = `
      <span class="ingredient-item__measure">${ing.measure || ''}</span>
      <span class="ingredient-item__name">${ing.name}</span>`;
    ul.appendChild(li);
  });

  // Instructions
  document.getElementById('instructionText').textContent =
    meal.instructions || 'No instructions available.';
}

function updateFavBtn(btn, isFav) {
  btn.textContent = isFav ? '♥ Saved' : '♡ Save';
  btn.classList.toggle('is-fav', isFav);
}

/* ── Helpers ────────────────────────────────────────────────────── */
function extractYtId(url) {
  if (!url) return null;
  const m = url.match(/(?:v=|youtu\.be\/)([^&?/]+)/);
  return m ? m[1] : null;
}

function setResultsTitle(title, count) {
  document.getElementById('resultsTitle').textContent = title;
  document.getElementById('resultsCount').textContent =
    count !== null ? `${count} meal${count !== 1 ? 's' : ''} found` : '';
}

function showSection(id) {
  ['categoriesSection', 'resultsSection', 'favouritesSection'].forEach(s => {
    document.getElementById(s).style.display = s === id ? 'block' : 'none';
  });
}

function setNavActive(link) {
  document.querySelectorAll('.nav__link').forEach(l => l.classList.remove('active'));
  if (link) link.classList.add('active');
}
