1. Copy template files to create your local configuration:

```bash
cp application.yml.template application.yml
cp application-dev.yml.template application-dev.yml
cp application-prod.yml.template application-prod.yml
```

2. Edit `application-dev.yml` and set your database password:

```yaml
spring:
  datasource:
    password: YOUR_ACTUAL_PASSWORD
```

3. For production, use environment variables in `application-prod.yml`:

```bash
export DATABASE_URL=jdbc:postgresql://your-host:5432/game_reviewer
export DATABASE_USERNAME=your_username
export DATABASE_PASSWORD=your_password