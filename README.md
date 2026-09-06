# Matches API

A REST API for managing sports matches and their odds. It is built with Java 21,
Spring Boot, Spring Data JPA, PostgreSQL, Flyway, and Maven.

## Run with Docker

Docker Compose starts both the API and PostgreSQL:

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080/api/v1`. Flyway creates the
database schema automatically when the application starts.

To stop the containers:

```bash
docker compose down
```

Add `-v` only when you also want to remove the local database volume.

## Run locally

Java 21 is required. Start just PostgreSQL with Docker and then run the app with
the included Maven Wrapper:

```bash
docker compose up -d database
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run` for the second command.

The default connection settings can be overridden with environment variables:

| Variable | Default |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/matches` |
| `DB_USERNAME` | `admin` |
| `DB_PASSWORD` | `admin` |
| `SERVER_PORT` | `8080` |

The checked-in credentials are intended only for local development.

## API

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/v1/auth/login` | Get a JWT access token |
| `GET` | `/api/v1/matches` | List all matches, including their odds |
| `POST` | `/api/v1/matches` | Create a match |
| `GET` | `/api/v1/matches/{matchId}` | Get one match |
| `PUT` | `/api/v1/matches/{matchId}` | Replace a match's details |
| `DELETE` | `/api/v1/matches/{matchId}` | Delete a match and its odds |
| `GET` | `/api/v1/matches/{matchId}/odds` | List the odds for a match |
| `POST` | `/api/v1/matches/{matchId}/odds` | Add an odd to a match |
| `GET` | `/api/v1/matches/{matchId}/odds/{oddId}` | Get one odd |
| `PUT` | `/api/v1/matches/{matchId}/odds/{oddId}` | Replace an odd |
| `DELETE` | `/api/v1/matches/{matchId}/odds/{oddId}` | Delete an odd |

The complete contract is in [`docs/openapi.yaml`](docs/openapi.yaml).

## Authentication

All `/api/v1/**` endpoints except `POST /api/v1/auth/login` require a JWT.
For local development, obtain a token with the default `admin` / `admin`
credentials:

```bash
curl -i -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

The response contains `accessToken`. Send it with every protected request:

```text
Authorization: Bearer <accessToken>
```

The username, password, signing key, and token lifetime are configurable with
`AUTH_USERNAME`, `AUTH_PASSWORD`, `JWT_SECRET` (a Base64-encoded key), and
`JWT_EXPIRATION_MINUTES`. The checked-in defaults are only for local development.

### Example

Create a football match:

```bash
curl -i -X POST http://localhost:8080/api/v1/matches \
  -H "Content-Type: application/json" \
  -d '{
    "description": "OSFP - PAO",
    "matchDate": "2026-09-10",
    "matchTime": "20:30:00",
    "teamA": "OSFP",
    "teamB": "PAO",
    "sport": "FOOTBALL"
  }'
```

Add an odd to the newly created match:

```bash
curl -i -X POST http://localhost:8080/api/v1/matches/1/odds \
  -H "Content-Type: application/json" \
  -d '{"specifier": "X", "odd": 1.500}'
```

Valid sports are `FOOTBALL` and `BASKETBALL`. Odds must be positive and may have
up to three decimal places. A specifier is unique within a match and is normalized
to uppercase.

Successful creates return `201 Created` and a `Location` header. Deletes return
`204 No Content`. Invalid requests return `400`, missing resources return `404`,
and duplicate specifiers return `409` with a consistent JSON error body.

## Tests

Run the test suite with:

```bash
./mvnw test
```

The tests cover the service rules and HTTP request/response behavior without
requiring a running database. The Docker workflow is the end-to-end path against
PostgreSQL.

## Postman

A ready-to-run collection and local environment are included in the
[`postman`](postman) directory. The collection performs a complete 13-request CRUD
workflow, carries created IDs between requests automatically, checks validation and
conflict responses, and cleans up its test data at the end.

## Implementation notes

The service uses a conventional controller-service-repository split. JPA handles
persistence, while Flyway owns schema creation and changes. Match responses embed
their odds; repository entity graphs load those collections in the same query to
avoid per-match database calls.

See [`docs/design.md`](docs/design.md) for the main design decisions, data model,
validation rules, and trade-offs.

