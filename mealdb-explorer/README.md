# 🍽 TheMealDB Explorer

A full-stack meal discovery app built with **Spring Boot**, **MySQL**, **HTML/CSS/JS**.

![TheMealDB Explorer](https://www.themealdb.com/images/logo-small.png)

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔍 Recipe Search | Search meals by name with instant results |
| 📂 Category Browser | Browse all meal categories visually |
| 🎲 Random Meal | "I'm Feeling Hungry" button for surprise recipes |
| 📖 Recipe Detail | Full ingredients, instructions & YouTube embed |
| ❤️ Favourites | Save meals to DB; persisted across sessions |
| ⚡ Smart Cache | Caffeine in-memory cache (30-min TTL, 500-entry limit) |
| 📱 Responsive | Works on mobile and desktop |
| 📄 Swagger UI | Interactive API documentation |

---

## 🏗 Architecture

```
mealdb-explorer/
├── backend/          # Spring Boot (Java 17)
│   └── src/main/java/com/mealdb/
│       ├── config/           # CacheConfig, CorsConfig, OpenApiConfig, RestTemplateConfig
│       ├── controller/       # MealController, CategoryController, AreaController,
│       │                     # FavouriteController, GlobalExceptionHandler
│       ├── dto/              # MealDto, MealSummaryDto, CategoryDto, ApiResponse
│       ├── model/            # Favourite (JPA Entity)
│       ├── repository/       # FavouriteRepository
│       └── service/          # MealDbService (cached), FavouriteService
│
└── frontend/         # Vanilla HTML + CSS + JS
    ├── index.html
    ├── css/style.css
    └── js/
        ├── api.js    # HTTP client layer
        ├── ui.js     # DOM rendering helpers
        └── app.js    # Application controller
```

---

## 🚀 Quick Start

### Prerequisites

| Tool | Version |
|---|---|
| Java | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ (optional — H2 used by default) |
| Live Server / any HTTP server | for frontend |

---

### 1. Clone

```bash
git clone https://github.com/YOUR_USERNAME/mealdb-explorer.git
cd mealdb-explorer
```

---

### 2. Start the Backend

#### Option A — H2 (in-memory, no setup needed)
```bash
cd backend
mvn spring-boot:run
```

#### Option B — MySQL
1. Create a database:
```sql
CREATE DATABASE mealdb_explorer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Update credentials in `src/main/resources/application-mysql.properties` or set env vars:
```bash
export DB_URL=jdbc:mysql://localhost:3306/mealdb_explorer?useSSL=false&serverTimezone=UTC
export DB_DRIVER=com.mysql.cj.jdbc.Driver
export DB_USER=root
export DB_PASS=your_password
```

3. Run with MySQL profile:
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

The API will start on **http://localhost:8080**

---

### 3. Start the Frontend

```bash
cd frontend
# Option 1: VS Code Live Server (right-click index.html → Open with Live Server)
# Option 2: Python
python3 -m http.server 5500
# Option 3: npx
npx serve . -p 5500
```

Open **http://localhost:5500** in your browser.

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/meals/search?q={name}` | Search meals by name |
| GET | `/api/v1/meals/{id}` | Get full meal detail |
| GET | `/api/v1/meals/random` | Get a random meal |
| GET | `/api/v1/meals/letter/{letter}` | Search by first letter |
| GET | `/api/v1/meals/ingredient?i={name}` | Filter by ingredient |
| GET | `/api/v1/categories` | List all categories |
| GET | `/api/v1/categories/{name}/meals` | Meals in a category |
| GET | `/api/v1/areas` | List all cuisine areas |
| GET | `/api/v1/areas/{area}/meals` | Meals by cuisine area |
| GET | `/api/v1/favourites` | Get all favourites |
| POST | `/api/v1/favourites` | Add a favourite |
| DELETE | `/api/v1/favourites/{mealId}` | Remove a favourite |

📄 **Swagger UI**: http://localhost:8080/swagger-ui.html  
📊 **Health Check**: http://localhost:8080/actuator/health

---

## ⚙️ Caching Strategy

| Cache | TTL | Max Entries | Notes |
|---|---|---|---|
| `meals` | 30 min | 500 | Search results |
| `mealDetail` | 30 min | 500 | Individual meal detail |
| `categories` | 30 min (+ scheduled 6h evict) | 500 | Category list |
| `byCategory` | 30 min | 500 | Meals per category |
| `areas` | 30 min | 500 | Cuisine area list |
| `byArea` | 30 min | 500 | Meals per area |
| `random` | — | — | Always live (not cached) |

---

## 🧪 Running Tests

```bash
cd backend
mvn test
```

---

## 🛠 Tech Stack

**Backend**
- Java 17
- Spring Boot 3.2
- Spring Cache + Caffeine (in-memory LRU cache)
- Spring Data JPA + Hibernate
- MySQL 8 / H2 (dev fallback)
- SpringDoc OpenAPI (Swagger)
- Lombok

**Frontend**
- Vanilla HTML5, CSS3, JavaScript (ES2020)
- Google Fonts (Playfair Display + DM Sans)
- No framework dependencies — zero build step

---

## 📁 Database Schema

```sql
CREATE TABLE favourites (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  meal_id   VARCHAR(20)  NOT NULL UNIQUE,
  meal_name VARCHAR(255) NOT NULL,
  thumbnail VARCHAR(500),
  category  VARCHAR(100),
  added_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
> Hibernate auto-creates this table on startup (`spring.jpa.hibernate.ddl-auto=update`).

---

## 📝 Notes

- TheMealDB API uses test key `1` (free public access).
- CORS is pre-configured for `localhost:3000`, `localhost:5500`, and `127.0.0.1:5500`.
- To add more allowed origins edit `cors.allowed-origins` in `application.properties`.

---

## 📜 License

MIT © 2024 TheMealDB Explorer
