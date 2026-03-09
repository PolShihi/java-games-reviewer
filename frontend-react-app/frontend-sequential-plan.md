# Frontend React: последовательный план работ

Дата обновления: 2026-03-09
Проект: `frontend-react-app`

## 1. API-контракт и базовая инфраструктура
- Backend base URL: `http://localhost:8088/api/v1`.
- Единый формат пагинации `PageResponse<T>`.
- Единый обработчик ошибок (`400/404/409/500`, валидация через `errors`).
- Централизованный API-слой: `games`, `genres`, `production-companies`, `media-outlets`, `reviews`, `system-requirements`, `system-requirement-types`.

## 2. Маршрутизация и каркас
- Маршруты:
- `/` -> `GamesPage`
- `/games/new` -> `GameFormPage`
- `/games/:id` -> `GameDetailsPage`
- `/games/:id/edit` -> `GameFormPage`
- Базовый layout с header и контейнером контента.

## 3. Список игр (Grid)
- Серверная пагинация, сортировка, загрузка через `GET /games`.
- Переход в детали игры.
- UI-состояния: `loading`, `empty`, `error`, `retry`.

## 4. Фильтры игр
- Запрос `GET /games/filter`.
- Фильтры:
- `title`, `yearFrom`, `yearTo`, `genreIds`
- `developerId`, `publisherId`
- `ratingFrom`, `ratingTo`
- Синхронизация фильтров с пагинацией и сортировкой.

## 5. Форма создания/редактирования игры
- `react-hook-form`.
- Загрузка справочников: `GET /genres`, `GET /production-companies`.
- Создание: `POST /games`.
- Редактирование: `GET /games/{id}` + `PUT /games/{id}`.
- Валидация UI + обработка `409/400`.

## 6. Управление справочниками без отдельных страниц (ReferenceManagerDialog)
- Реализовать единый переиспользуемый модал `ReferenceManagerDialog`.
- Режимы работы:
- `genres`
- `production-companies`
- `media-outlets`
- Внутри модала:
- список записей
- `Create`
- `Edit`
- `Delete` c подтверждением
- локальный поиск (client-side)
- После операций делать refresh родительских списков селектов.

## 7. Интеграция справочников в форму игры
- В `GameFormPage` рядом с полями `Genres`, `Developer`, `Publisher` добавить кнопку `Manage`.
- Кнопка открывает `ReferenceManagerDialog` в нужном режиме.
- После `Create/Edit/Delete` мгновенно обновлять options в селектах.
- Не добавлять отдельные страницы просмотра жанров/компаний.

## 8. Страница деталей игры (read-first)
- `GET /games/{id}`.
- Отображение:
- базовой информации об игре
- рейтинга
- таблицы системных требований
- списка обзоров
- Действия: `Edit game`, `Delete game`.

## 9. Модалка добавления системных требований
- Типы: `GET /system-requirement-types`.
- Создание: `POST /system-requirements`.
- Поля: `gameId`, `systemRequirementTypeId`, `storageGb`, `ramGb`, `cpuGhz`, `gpuTflops`, `vramGb`.
- Обновление блока требований после сохранения.

## 10. Модалка добавления/редактирования обзора + управление media outlets
- Список изданий: `GET /media-outlets`.
- Создание/редактирование обзора: `POST/PUT /reviews`.
- В `AddReviewDialog` добавить кнопку `Manage outlets`, открывающую `ReferenceManagerDialog` в режиме `media-outlets`.
- После изменений медиа-изданий обновлять список выбора без перезагрузки страницы.

## 11. Финализация
- E2E-сценарий:
- создать игру
- добавить/изменить справочники из модалок
- добавить системные требования
- добавить обзор
- отредактировать и удалить игру
- Прогнать `npm run lint`.
- Обновить README: запуск, env, ключевые user-flow.

## 12. Правило выполнения
- Реализация этапами по запросу, без генерации всего проекта за один шаг.
