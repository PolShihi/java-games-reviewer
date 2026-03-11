# React UI + Backend API Audit (для переноса в Angular)

Дата: 2026-03-11
Источник: `frontend-react-app`, `backend-app`

## 1. Базовые настройки UI
- UI библиотека: MUI.
- Тема (src/main.jsx):
  - `palette.mode = light`
  - `primary.main = #1976d2`
  - `CssBaseline` включен.
- Фон приложения: `#f4f6f8`.
- Общий контейнер контента: `Container maxWidth="lg"`, отступы `py: 3`.

## 2. Маршруты (src/App.jsx)
- `/` -> `GamesPage`
- `/games/new` -> `GameFormPage` (mode="create")
- `/games/:id` -> `GameDetailsPage`
- `/games/:id/edit` -> `GameFormPage` (mode="edit")
- `/home` -> редирект на `/`
- `*` -> `NotFoundPage`

## 3. Layout (src/components/layout/AppLayout.jsx)
- Sticky AppBar с нижней границей.
- Лого-кнопка слева:
  - иконка `SportsEsportsRoundedIcon`
  - текст `Games Reviewer` (h6, weight 700)
  - ведет на `/`
- Справа кнопка `Add Game`:
  - `variant="contained"`, `color="secondary"`
  - иконка `AddCircleOutlineRoundedIcon`
  - ведет на `/games/new`

## 4. Страница списка игр (src/pages/GamesPage.jsx)
### 4.1 Состояния
- `loading` (spinner по центру, если список пуст)
- `error` (Alert + Retry)
- `empty` (Alert "No games found.")

### 4.2 Таблица
- Колонки:
  - `ID` (sortable)
  - `Title` (sortable)
  - `Release Year` (sortable)
  - `Genres` (chips, не sortable)
  - `Developer` (не sortable)
  - `Publisher` (не sortable)
  - `Rating` (sortable, sortField = `averageRating`)
  - `Actions` (иконка глаз)
- Клик по строке -> переход `/games/:id`.
- Rating:
  - если есть `averageRating`, то `Rating` (value = averageRating/20, precision 0.5) + число (округление)
  - иначе "N/A"

### 4.3 Пагинация и сортировка
- `TablePagination` с options `[1, 5, 10, 20, 50]`.
- Sort:
  - по полю `sortBy` и направлению `sortDirection` (ASC/DESC)
  - при смене сортировки сброс страницы в 0.

### 4.4 Фильтры
- Поля:
  - `title` (TextField)
  - `yearFrom` (number, min 1950)
  - `yearTo` (number, min 1950)
  - `genreIds` (multi-select)
  - `developerId` (select, список всех компаний)
  - `publisherId` (select, список всех компаний)
  - `ratingFrom` (number, min 0, max 100)
  - `ratingTo` (number, min 0, max 100)
- Кнопки:
  - `Apply` -> применяет фильтры
  - `Reset` -> сброс фильтров и пагинации
- Если активны фильтры -> запрос идет на `GET /games/filter`, иначе `GET /games`.

## 5. Страница формы игры (src/pages/GameFormPage.jsx)
### 5.1 Режимы
- `mode="create"` -> заголовок "Create Game", submit -> `POST /games`
- `mode="edit"` -> заголовок "Edit Game", загрузка `GET /games/{id}`, submit -> `PUT /games/{id}`

### 5.2 Поля
- Title (required, max 150)
- Release year (required, min 1950)
- Description (optional, textarea)
- Developer (select, optional)
- Publisher (select, optional)
- Genres (multi-select, required минимум 1)

### 5.3 Управление справочниками
- Кнопки `Manage` возле:
  - Developer -> ReferenceManagerDialog entityType `production-companies`
  - Publisher -> ReferenceManagerDialog entityType `production-companies`
  - Genres -> ReferenceManagerDialog entityType `genres`
- После изменения справочников -> reload справочников.

### 5.4 Действия
- Cancel: возвращает на `/` или `/games/:id` в edit-mode
- Submit: `Create game` или `Save changes`
- Ошибки:
  - если `error.errors` есть -> показывается первое сообщение
  - иначе `error.message`

## 6. Страница деталей игры (src/pages/GameDetailsPage.jsx)
### 6.1 Верхний блок
- Заголовок (title), release year
- Rating (averageRating/20), либо "Rating: N/A"
- Кнопки:
  - Back -> `/`
  - Edit -> `/games/:id/edit`
  - Delete -> confirm -> `DELETE /games/{id}`

### 6.2 About
- Description (fallback: "No description provided.")
- Developer / Publisher
- Chips жанров

### 6.3 System Requirements (таблица + модалка)
- Таблица:
  - Type, Storage (GB), RAM (GB), CPU (GHz), GPU (TFLOPS), VRAM (GB), Actions
- Кнопка `Add requirement` -> AddRequirementDialog
- Edit/Delete для каждой строки

### 6.4 Reviews (карточки + модалка)
- Карточки:
  - Outlet name
  - Score
  - Summary
  - Edit/Delete
- Кнопка `Add review` -> AddReviewDialog

## 7. ReferenceManagerDialog (src/components/reference/ReferenceManagerDialog.jsx)
### 7.1 Сущности
- `genres`
- `production-companies`
- `media-outlets`

### 7.2 Поля формы
- Genres: `name`
- Media outlets: `name`, `websiteUrl`, `foundedYear`
- Production companies: `name`, `websiteUrl`, `foundedYear`, `ceo`, `companyTypeId`

### 7.3 Функции
- Create/Edit/Delete
- Локальный поиск (name / website / ceo)
- Refresh
- Таблица с колонками:
  - Genres: ID, Name, Actions
  - Media outlets: ID, Name, Website, Founded, Actions
  - Production companies: ID, Name, Website, Founded, CEO, Company type, Actions

## 8. AddRequirementDialog (src/components/system-requirements/AddRequirementDialog.jsx)
- Используется для create/edit.
- Поля:
  - `systemRequirementTypeId` (required)
  - `storageGb` (required, min 1)
  - `ramGb` (required, min 1)
  - `cpuGhz` (optional, step 0.1)
  - `gpuTflops` (optional, step 0.01)
  - `vramGb` (optional)
- Submit:
  - create -> `POST /system-requirements`
  - edit -> `PUT /system-requirements/{id}`

## 9. AddReviewDialog (src/components/reviews/AddReviewDialog.jsx)
- Используется для create/edit.
- Поля:
  - `mediaOutletId` (required)
  - `score` (required, 0..100)
  - `summary` (optional)
- Кнопка `Manage` -> ReferenceManagerDialog entityType `media-outlets`.
- Submit:
  - create -> `POST /reviews`
  - edit -> `PUT /reviews/{id}`

## 10. API и обработка ошибок (frontend-react-app)
- Base URL: `VITE_API_URL` или `http://localhost:8088/api/v1`.
- Axios interceptor:
  - Все ошибки приводятся к `ApiError`:
    - `message`, `status`, `errors`, `timestamp`, `method`, `url`, `originalError`
- Утилита `normalizePageResponse`:
  - принимает либо массив, либо объект `PageResponse` и нормализует.

## 11. Backend endpoints (backend-app)
Base path: `/api/v1`

### 11.1 Games
- `GET /games` (page,size,sortBy,sortDirection) -> `PageResponse<GameListResponse>`
- `GET /games/{id}` -> `GameDetailResponse`
- `GET /games/filter` (title, yearFrom, yearTo, genreIds, developerId, publisherId, ratingFrom, ratingTo, page, size, sortBy, sortDirection)
- `POST /games` -> `GameDetailResponse`
- `PUT /games/{id}` -> `GameDetailResponse`
- `DELETE /games/{id}` -> 204

### 11.2 Genres
- `GET /genres` -> `List<GenreResponse>`
- `GET /genres/{id}`
- `POST /genres`
- `PUT /genres/{id}`
- `DELETE /genres/{id}`

### 11.3 Production Companies
- `GET /production-companies` (page,size,sortBy,sortDirection) -> `PageResponse<ProductionCompanyResponse>`
- `GET /production-companies/{id}`
- `POST /production-companies`
- `PUT /production-companies/{id}`
- `DELETE /production-companies/{id}`

### 11.4 Media Outlets
- `GET /media-outlets` (page,size,sortBy,sortDirection) -> `PageResponse<MediaOutletResponse>`
- `GET /media-outlets/{id}`
- `POST /media-outlets`
- `PUT /media-outlets/{id}`
- `DELETE /media-outlets/{id}`

### 11.5 Reviews
- `GET /reviews` (page,size,sortBy,sortDirection) -> `PageResponse<ReviewResponse>`
- `GET /reviews/game/{gameId}` (page,size) -> `PageResponse<ReviewResponse>` (sort by score DESC)
- `GET /reviews/{id}`
- `POST /reviews`
- `PUT /reviews/{id}`
- `DELETE /reviews/{id}`

### 11.6 System Requirements
- `GET /system-requirements` (page,size,sortBy,sortDirection) -> `PageResponse<SystemRequirementResponse>`
- `GET /system-requirements/game/{gameId}` (page,size)
- `GET /system-requirements/{id}`
- `POST /system-requirements`
- `PUT /system-requirements/{id}`
- `DELETE /system-requirements/{id}`

### 11.7 Reference (read-only)
- `GET /system-requirement-types`
- `GET /system-requirement-types/{id}`
- `GET /company-types`
- `GET /company-types/{id}`

## 12. DTO поля (backend-app)
- `GameListResponse`: `id`, `title`, `releaseYear`, `developerName`, `publisherName`, `genreNames[]`, `averageRating`
- `GameDetailResponse`: `id`, `title`, `releaseYear`, `description`, `developer`, `publisher`, `genres[]`, `systemRequirements[]`, `reviews[]`, `averageRating`
- `ProductionCompanyResponse`: `id`, `name`, `foundedYear`, `websiteUrl`, `ceo`, `companyTypeName`
- `MediaOutletResponse`: `id`, `name`, `websiteUrl`, `foundedYear`
- `ReviewResponse`: `id`, `gameId`, `gameTitle`, `mediaOutlet`, `score`, `summary`
- `SystemRequirementResponse`: `id`, `gameId`, `type`, `storageGb`, `ramGb`, `cpuGhz`, `gpuTflops`, `vramGb`
- `SystemRequirementTypeResponse`: `id`, `name`
- `CompanyTypeResponse`: `id`, `name`

## 13. Валидации (backend-app)
- Game title: required, max 150.
- Release year: min 1950.
- Genre name: required, max 50.
- Production company name: required, max 100; websiteUrl max 255; ceo max 100; foundedYear min 1900.
- Media outlet name: required, max 100; websiteUrl max 255; foundedYear min 1900.
- Review: gameId, mediaOutletId, score required; score 0..100.
- SystemRequirement: gameId, typeId, storageGb, ramGb required; storage/ram min 1.
