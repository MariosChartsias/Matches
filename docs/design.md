# Design and implementation notes

## Scope

The service manages two resources:

- A **match** contains its description, date, time, participating teams, and sport.
- A **match odd** belongs to exactly one match and contains a specifier and decimal
  price.

Both resources expose create, read, update, and delete operations. Odds use nested
URLs because an odd has no meaning outside its parent match.

## Data model

```text
matches
  id              bigint, primary key
  description     varchar(200)
  match_date      date
  match_time      time
  team_a          varchar(100)
  team_b          varchar(100)
  sport           varchar(20)
       |
       | 1:N
       v
match_odds
  id              bigint, primary key
  match_id        bigint, foreign key
  specifier       varchar(50)
  odd             numeric(10,3)
```

The database enforces the rules that remain important even if data is written
outside the API: valid sports, different teams, positive odds, unique specifiers
per match, and referential integrity. Deleting a match cascades to its odds.

Dates and times are deliberately stored separately because they are separate
fields in the requested model. The API uses ISO-8601 representations (`YYYY-MM-DD`
and `HH:mm:ss`) and Java's `LocalDate`/`LocalTime`; no timezone is implied.

## Application structure

```text
HTTP request
    -> controller (routing and status codes)
    -> service (transactions and business rules)
    -> repository (JPA queries)
    -> PostgreSQL
```

Request and response records keep the HTTP model separate from the JPA entities.
This prevents persistence details from leaking into JSON and avoids recursive
serialization across the match-to-odds relationship.

Read operations use read-only transactions. Write operations have explicit
transaction boundaries in the service layer. Match detail queries use a JPA
entity graph. The paginated match list loads odds in batches, allowing the database
to apply the page limit without issuing one odds query per match.

## Validation and errors

Input is checked before it reaches the service layer:

- required text cannot be blank and has a database-aligned maximum length;
- both date and time are required;
- the teams must differ, ignoring surrounding whitespace and case;
- sport must be `FOOTBALL` or `BASKETBALL`;
- an odd must be greater than zero with at most three decimal places.

Specifiers are trimmed and converted to uppercase. Reusing a specifier for the
same match returns `409 Conflict`; the same specifier may be used by another match.

All expected failures return the same error shape with a timestamp, HTTP status,
message, request path, and optional field validation details. Internal database
messages are not exposed.

## Database lifecycle

Hibernate validates the entity mapping but does not create or modify tables.
Flyway is the single source of truth for the schema, starting with
`V1__create_match_schema.sql`. Future schema changes should be added as new
versioned migrations rather than editing a migration that has already shipped.

## Deployment

The Docker image is built in two stages. Maven and the JDK remain in the build
stage; the runtime image contains only a Java runtime and the application jar. The
process runs as an unprivileged user. Docker Compose supplies PostgreSQL, waits for
its health check, and then starts the API.

Production deployments should provide database credentials through a secret
manager, pin container images by digest, terminate TLS at the platform edge, and
add authentication according to the consuming system's requirements. Those
concerns are intentionally outside this assessment's CRUD scope.

