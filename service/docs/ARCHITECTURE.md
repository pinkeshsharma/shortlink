# Architecture & Code Layout

The system is built with:

- **Java 21**, **Spring Boot 3.5.x**
- **PostgreSQL** for persistence (JPA/Hibernate)
- **Redis** for caching lookups
- **springdoc-openapi** for API docs

## Code Layout

```
main
├─ java.com.shortlink
   ├─ ShortLinkApplication.java
   ├─ ServletInitializer.java
   ├─ config/RedisConfig.java, CorsConfig.java
   ├─ controller/ShortLinkController.java
   ├─ dto/ShortLinkRequest.java, ShortLinkResponse.java
   ├─ db/entity/ShortLink.java, DeadLetter.java
   ├─ db/ShortLinkRepository.java, DeadLetterRepository.java
   └─ service/ShortLinkService.java, ShortLinkServiceImpl.java
└─ resources
    ├─ application.yml
    └─ schema.sql
```
