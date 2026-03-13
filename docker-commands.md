# Docker Commands Cheat Sheet

## Build and Run
1. `docker compose build`
2. `docker compose up -d`
3. `docker compose up --build`
4. `docker compose down`
5. `docker compose down -v`

## Logs
1. `docker compose logs -f`
2. `docker compose logs -f backend`
3. `docker compose logs -f frontend-react`
4. `docker compose logs -f frontend-angular`
5. `docker compose logs -f db`

## Containers
1. `docker compose ps`
2. `docker compose restart backend`
3. `docker compose stop frontend-angular`
4. `docker compose start frontend-angular`

## Rebuild Specific Service
1. `docker compose build backend`
2. `docker compose up -d --no-deps --build backend`

## Database
1. `docker exec -it games-reviewer-db psql -U postgres -d games_reviewer_db`
2. `docker exec -it games-reviewer-db psql -U postgres -d games_reviewer_db -c "\\dt"`
3. `docker exec -it games-reviewer-db psql -U postgres -d games_reviewer_db -c "SELECT * FROM games LIMIT 5;"`

## Cleanup
1. `docker system prune`
2. `docker volume prune`
3. `docker image prune`

## Troubleshooting
1. `docker compose config`
2. `docker inspect games-reviewer-backend`
3. `docker inspect games-reviewer-frontend-react`
4. `docker inspect games-reviewer-frontend-angular`
