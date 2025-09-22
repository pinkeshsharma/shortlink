# Architecture & Code Layout

The system is built with:

- **Java 21**, **Spring Boot 3.5.x**
- **PostgreSQL** for persistence (JPA/Hibernate)
- **Redis** for caching lookups
- **springdoc-openapi** for API docs

## Code Layout

```
com.shortlink
├─ ShortLinkApplication.java
├─ controller/ShortLinkController.java
├─ service/ShortLinkService.java, ShortLinkServiceImpl.java
├─ db/entity/ShortLink.java, DeadLetter.java
├─ db/ShortLinkRepository.java, DeadLetterRepository.java
├─ config/RedisConfig.java
└─ resources/schema.sql
```
