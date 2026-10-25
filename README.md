# URL Shortener with Analytics

Production-ready **Spring Boot 3** project implementing a URL shortener with click analytics.

## Features
- Create short URLs
- Redirect short code to original URL
- Expiration support
- Click analytics (count + timestamps)
- Top links by clicks
- Redis cache for fast redirects
- PostgreSQL persistence + Flyway migrations
- OpenAPI docs, Docker, CI, tests

## Run
```bash
docker compose up --build
```
