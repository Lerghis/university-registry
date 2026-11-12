# University Registry System

A REST API for managing students, professors, courses, enrollments and grades at a university.

Built as a learning project in stages: first a console application with handwritten JDBC, then converted layer by layer to Spring Boot, Spring Data JPA and a REST API. Every Spring feature replaces something that was first written by hand.

## Tech stack

- Java 21, Maven
- Spring Boot 4.1 (Spring MVC, Spring Data JPA)
- Hibernate (JPA) and HikariCP
- MariaDB
- Bean Validation
- JUnit 5 and Mockito

## Architecture

Layered, each layer talking only to the one below it:

```
Client -> web (controllers, DTOs, exception handler) -> service -> repository -> MariaDB
```

The `model` and `exception` packages are shared by every layer. See [ARCHITECTURE.md](ARCHITECTURE.md) for the design decisions and the reasons behind them.

## Running it

1. Install MariaDB, create the database and run the schema:
   ```sql
   CREATE DATABASE university_registry CHARACTER SET utf8mb4;
   ```
   then run `docs/schema.sql` against it.
2. Create a database user with access to that database only.
3. Copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties` and fill in the URL, username and password. This file is git-ignored.
4. Start `RegistryApplication` from the IDE, or run `mvn spring-boot:run`.
5. The API is now available at `http://localhost:8080`.

## API overview

| Resource | Requests |
|---|---|
| Students | `GET /students`, `GET /students/{am}`, `POST /students`, `PUT /students/{am}`, `DELETE /students/{am}` |
| Professors | `GET /professors`, `GET /professors/{profId}`, `POST /professors`, `PUT /professors/{profId}`, `DELETE /professors/{profId}` |
| Courses | `GET /courses`, `GET /courses/{courseId}`, `POST /courses`, `PUT /courses/{courseId}`, `DELETE /courses/{courseId}` |
| Enrollments | `POST` / `DELETE /students/{am}/courses/{courseId}`, `GET /students/{am}/courses` |
| Assignments | `POST` / `DELETE /professors/{profId}/courses/{courseId}`, `GET /professors/{profId}/courses` |
| Grades | `POST /students/{am}/grades`, `GET /students/{am}/grades`, `PUT` / `DELETE /students/{am}/grades/{courseId}` |
| Averages | `GET /students/{am}/average`, `GET /courses/{courseId}/average` |

Success statuses: `200` for reads and updates, `201` for creates, `204` for deletes.

## Errors

Every error uses the same JSON shape (RFC 9457 `ProblemDetail`):

```json
{"type":"about:blank","title":"Not Found","status":404,"detail":"..."}
```

| Status | Meaning |
|---|---|
| `400` | The request is invalid (missing or too long field, invalid semester or grade, malformed JSON) |
| `404` | The student, course, professor, enrollment or grade does not exist, or there are no grades to average |
| `409` | The request conflicts with existing data (duplicate, or still referenced by other records) |
| `500` | A database or unexpected failure; details are logged, never returned |

## Example requests

```bash
# create a student
curl -i -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -d '{"am":"AM1","semester":1,"name":"Maria","email":"m@x.gr","phone":"100"}'

# list students
curl -i http://localhost:8080/students

# enroll the student in a course
curl -i -X POST http://localhost:8080/students/AM1/courses/C1

# record a grade
curl -i -X POST http://localhost:8080/students/AM1/grades \
  -H "Content-Type: application/json" \
  -d '{"courseId":"C1","value":8.5}'

# read the student's average
curl -i http://localhost:8080/students/AM1/average
```

## Project status

- [x] Layered structure, domain model and exception hierarchy
- [x] Business rules in the service layer
- [x] MariaDB schema with foreign keys and constraints
- [x] Spring Data JPA persistence
- [x] REST API with DTOs, validation and a global exception handler
- [ ] Automated tests for the remaining services and the controllers
- [ ] Authentication and role-based access (Spring Security)

## Milestones (git tags)

| Tag | State |
|---|---|
| `v0.1-in-memory-services` | Services backed by in-memory collections |
| `v0.2-services-use-daos` | Hand-written JDBC DAOs and HikariCP |
| `v0.3-console-ui` | Complete console application |
| `v0.4-rest-api` | Spring Boot REST API |