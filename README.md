# Testing day application stub

* [Assignment](https://docs.google.com/document/d/1_N8WLocdokIcb6KhfmqLUcMkvevoOqmHFRRDBA3MUzw/edit#)

Use this application skeleton to implement an exchange rates provider for our other services. The first version of the
application will be implemented as a caching layer over a 3rd-party service.

https://github.com/shipmonk-rnd/exchange-rates-task-stub
We don’t want you to spend days with this task, therefore if you’ll have to decide between finishing all functionality
or designing app architecture, give the architecture a priority. Nicely architected application, but not fully
functional will get more points than badly architected but working app.

Use https://fixer.io/ as exchange rates source
Register a new account for development and testing on your personal email

Base currency is USD

Response shape of /api/v1/rates/{day} should be similar to what https://fixer.io/documentation responds

Use PostgreSQL to store the cached exchange rates

Start a local database for development using docker-compose up -d database

You can use whatever libraries you may need

If you have experience with Hibernate, please use it to demonstrate your knowledge

The service will be used only internally, so securing the API is not needed
Prioritize making the code testable, don’t spend too much time writing tests for everything at the expense of other
requirements. But if you want and have time for it, you can write a test or two.

Think of edge cases that can occur in this environment and design solid error handling so that the downstream services
can be simplified as much as reasonably possible.

See test.sh for example requests

## Pre-requisites
* Java 21
* Maven
* Fixer API key (free plan is enough) https://fixer.io/



## Build

```bash
  ./mvnw clean package
```

**Notes**:

- In order to run tests, local database should be running on `localhost:5432`.
  Use the script below to start it.
    ```bash
      docker/start-env.sh
    ```
- You can skip tests during build using `-DskipTests` maven flag.
- **Future improvement**: Use `test containers` library to start a temporary database during tests.

### Run

```bash
  export FIXER_API_KEY=<your api key is here>
  java -jar target/testingday-exchange-rates-0.0.1-SNAPSHOT.jar
```

**Note**: Local database should be running on default port 5432. Use the script below to start it.
```bash
  docker/start-env.sh
```

### Test

```bash
  curl -v http://localhost:8080/api/v1/rates/2022-06-20
```

## High level design

The application is built using Spring Boot framework and Hibernate.

It exposes a REST API endpoint to fetch exchange rates for a given date.

The application uses PostgresSQL as a persisted caching layer to store exchange rates fetched from the Fixer.io API.

The app uses in-memory caching (Caffeine lib) to store exchange rates for the duration of the application runtime (up to 180 rate snapshots).
This reduces the number of database calls for frequently requested dates.

For administration purpose there is an endpoint to clear in-memory cache:

```bash
  curl -X POST http://localhost:8080/api/v1/admin/cache/invalidate
```

Liquibase is used for database schema management. The schema is automatically created/updated on application startup.

There is `app.rate.fixer.mock` property to use a mock Fixer.io API implementation for testing purposes to avoid using real API calls
and save Fixer API quota during development.

## Possible Future improvements

- Add more tests including integration ones.
- Run PostgresSQL in a container during tests using `test containers` library.
- Add authentication/authorization to call at least `/api/v1/admin/*` endpoints.
- Make in-memory cache size configurable.
- Add a background job to pre-fetch and cache exchange rates for the last N days.
- Fill in in-memory cache on application startup with the last N days exchange rates from the database.
- Use `mapstruct` (or similar library) to map entities to DTOs and vice versa.
