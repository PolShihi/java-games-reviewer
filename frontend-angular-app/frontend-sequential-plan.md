# Frontend Angular: последовательный план работ

Дата обновления: 2026-03-11
Проект: `frontend-angular-app`

## 1. Аудит источников и API-контракта
- Изучить `frontend-react-app` на предмет структуры UI, компонентов, состояния, маршрутов и стилистики.
- Зафиксировать основные сценарии и визуальные паттерны для точного воспроизведения в Angular.
- Подтвердить базовый API URL: `http://localhost:8088/api/v1`.
- Подтвердить список endpoint-ов и форматы ответов (включая пагинацию и ошибки).

## 2. Базовая инфраструктура Angular
- Создать Angular app, настроить структуру директорий.
- Настроить базовый API-клиент (HttpClient + interceptors).
- Ввести общий тип `PageResponse<T>` и единый обработчик ошибок.
- Подготовить сервисы: `games`, `genres`, `production-companies`, `media-outlets`, `reviews`, `system-requirements`, `system-requirement-types`, `company-types`.

## 3. Маршрутизация и каркас
- Маршруты:
- `/` -> `GamesPage`
- `/games/new` -> `GameFormPage`
- `/games/:id` -> `GameDetailsPage`
- `/games/:id/edit` -> `GameFormPage`
- Базовый layout с header и контейнером контента.

## 4. Список игр (Grid)
- Серверная пагинация, сортировка, загрузка через `GET /games`.
- Переход в детали игры.
- UI-состояния: `loading`, `empty`, `error`, `retry`.

## 5. Фильтры игр
- Запрос `GET /games/filter`.
- Фильтры:
- `title`, `yearFrom`, `yearTo`, `genreIds`
- `developerId`, `publisherId`
- `ratingFrom`, `ratingTo`
- Синхронизация фильтров с пагинацией и сортировкой.

## 6. Форма создания/редактирования игры
- Reactive Forms.
- Загрузка справочников: `GET /genres`, `GET /production-companies`.
- Создание: `POST /games`.
- Редактирование: `GET /games/{id}` + `PUT /games/{id}`.
- Валидация UI + обработка `409/400`.

## 7. Управление справочниками без отдельных страниц (ReferenceManagerDialog)
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

## 8. Интеграция справочников в форму игры
- В `GameFormPage` рядом с полями `Genres`, `Developer`, `Publisher` добавить кнопку `Manage`.
- Кнопка открывает `ReferenceManagerDialog` в нужном режиме.
- После `Create/Edit/Delete` мгновенно обновлять options в селектах.
- Не добавлять отдельные страницы просмотра жанров/компаний.

## 9. Страница деталей игры (read-first)
- `GET /games/{id}`.
- Отображение:
- базовой информации об игре
- рейтинга
- таблицы системных требований
- списка обзоров
- Действия: `Edit game`, `Delete game`.

## 10. Модалка добавления системных требований
- Типы: `GET /system-requirement-types`.
- Создание: `POST /system-requirements`.
- Поля: `gameId`, `systemRequirementTypeId`, `storageGb`, `ramGb`, `cpuGhz`, `gpuTflops`, `vramGb`.
- Обновление блока требований после сохранения.

## 11. Модалка добавления/редактирования обзора + управление media outlets
- Список изданий: `GET /media-outlets`.
- Создание/редактирование обзора: `POST/PUT /reviews`.
- В `AddReviewDialog` добавить кнопку `Manage outlets`, открывающую `ReferenceManagerDialog` в режиме `media-outlets`.
- После изменений медиа-изданий обновлять список выбора без перезагрузки страницы.

## 12. Финализация
- E2E-сценарий:
- создать игру
- добавить/изменить справочники из модалок
- добавить системные требования
- добавить обзор
- отредактировать и удалить игру
- Прогнать `npm run lint`.
- Обновить README: запуск, env, ключевые user-flow.

## 13. Правило выполнения
- Реализация этапами по запросу, без генерации всего проекта за один шаг.
