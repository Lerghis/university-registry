# Architecture & Design Notes

Internal reference for the *why* behind this project. Updated for the REST API version (tag `v0.4-rest-api`).

## 1. The big picture

```
Client -> web -> service -> repository -> MariaDB
          (controllers,     (business     (Spring Data JPA
           DTOs, handler)    rules)         and Hibernate)
```

`model` (entities) and `exception` are shared by every layer. Each layer talks only to the one directly below it and never skips one.

The project was built in stages on purpose, and each stage is a git tag:

| Tag | State |
|---|---|
| `v0.1-in-memory-services` | Services backed by `HashMap` / `List` |
| `v0.2-services-use-daos` | Hand-written JDBC DAOs and HikariCP |
| `v0.3-console-ui` | Complete console application |
| `v0.4-rest-api` | Spring Boot REST API (this version) |

Writing each layer by hand first is what makes the framework understandable: every Spring feature replaces something that was written manually before.

## 2. Layers

| Package | Responsibility | Must not contain |
|---|---|---|
| `model` | Domain classes and JPA entities, with self-validation (constructors and setters throw on invalid state) | SQL, HTTP, rules about other entities |
| `exception` | The domain exception hierarchy (section 6) | Logic |
| `repository` | Spring Data interfaces; they report facts about stored data | Business rules |
| `service` | Business rules: existence checks, duplicate checks, safe deletes, averages | SQL, HTTP, DTOs |
| `web` | Controllers (thin translators) and `GlobalExceptionHandler` | Business rules, repositories |
| `web.dto` | Request and response records: the API's own shape | Logic beyond mapping |

## 3. Object graph

Spring creates every object (a "bean") at startup and passes dependencies through constructors. `@SpringBootApplication` scans from `com.university.registry` downward.

Dependencies point one way only (Acyclic Dependencies Principle), and with constructor injection Spring refuses to start if there is a cycle. That is also why the safe-delete methods live in `EnrollmentService`: it already depends on all three entity services, so putting them anywhere else would create a cycle.

```
GradeService      -> StudentService, CourseService, EnrollmentService, GradeRepository
EnrollmentService -> StudentService, ProfessorService, CourseService,
                     StudentCoursesRepository, ProfessorCoursesRepository
```

## 4. Repository vs service: facts vs decisions

The repository reports **facts**; the service decides what they **mean**.

| Repository (fact) | Service (rule) |
|---|---|
| `findById` returned an empty `Optional` | becomes `EntityNotFoundException` |
| `existsById` returned `true` when adding | becomes `DuplicateEntityException` |
| `existsByStudentAm` returned `false` for grades | becomes `NoGradesRecordedException` |
| `existsByCourseId` returned `true` when deleting | becomes `EntityInUseException` |

Two cautions:

- `save()` inserts **or** updates, so it silently overwrites an existing key. The service's existence check before saving is essential.
- `avg` over zero rows is `NULL` (a `null` `Double`), so the service checks that grades exist before asking for an average.

## 5. Services are stateless

No service holds data. They hold references to repositories and other services, and the database is the single source of truth. Services are therefore safe to share between the many request threads Tomcat runs.

## 6. Exception design

```
Throwable
  `-- Exception
        `-- RuntimeException
              |-- RegistryException (abstract)      domain rule violations
              |     |-- InvalidSemesterException
              |     |-- InvalidGradeException
              |     |-- DuplicateEntityException
              |     |-- EntityNotFoundException
              |     |-- EntityInUseException
              |     `-- NoGradesRecordedException
              `-- DataAccessException (Spring)      infrastructure failures
                    `-- DataIntegrityViolationException
```

All domain exceptions are **unchecked**. Spring's handler methods and framework interfaces do not declare checked exceptions, one handler class deals with every domain error, and Spring's transactions roll back on unchecked exceptions by default.

| Exception | HTTP status |
|---|---|
| `EntityNotFoundException`, `NoGradesRecordedException` | 404 |
| `DuplicateEntityException`, `EntityInUseException` | 409 |
| `InvalidSemesterException`, `InvalidGradeException` | 400 |
| Bean Validation failure (`MethodArgumentNotValidException`) | 400, with the list of invalid fields |
| `DataIntegrityViolationException` (a database constraint fired) | 409, generic text |
| `DataAccessException` | 500, generic text; details go to the log |

Domain messages are written for users and are returned as they are. Infrastructure messages are never returned.

## 7. Web layer

- **Controllers are thin translators:** request in, service call, response out. No business rules.
- **DTOs (Java records)** are the API's own shape, kept separate from the entities: the API survives schema changes, only intended fields are exposed, and update bodies omit the id that the URL already carries. Mapping between DTOs and entities is done by hand.
- **Validation:** the DTOs check the *format* (a required field is present and fits the column length) with Bean Validation, and the model checks the *rules* (semester of at least 1, grade from 0 to 10). One rule has one owner, so no rule is duplicated.
- **Errors** use one shape everywhere (`ProblemDetail`, RFC 9457).
- **API design:** plural nouns, the HTTP verb is the action and the status code is the outcome. Relationships use nested URLs (`/students/{am}/courses/{courseId}`). `PUT` replaces, `POST` creates (201), `DELETE` answers 204. Deletes go through the `...Safely` methods of `EnrollmentService`.

## 8. Persistence rules

- Entities map onto the handwritten schema, and `ddl-auto=validate` makes a mismatch stop startup. Never use `create`, `create-drop` or `update`.
- `@Id` on a field means field access: Hibernate bypasses constructors and setters, so model validation does not run when rows are loaded. The database constraints are the second line of defense.
- Composite keys use `@IdClass`. `StudentCourseId` is shared by `StudentCourses` and `Grade`, which have the same key shape, and `ProfessorCourseId` serves `ProfessorCourses`. Key classes implement `equals` and `hashCode`.
- Derived queries (`existsByStudentAm`) cover simple lookups. JPQL (`@Query`) covers the joins and the averages. The join avoids the N+1 query problem.
- `grade_value` is `DECIMAL(4,2)` in the database and a `float` in Java, mapped with `columnDefinition`.
- Secrets: `db.properties` is git-ignored and imported with `spring.config.import`; only `db.properties.example` is committed.

## 9. Naming conventions

- Database: `snake_case`, plural table names. Java: `camelCase`. Spring's default naming maps `courseId` to `course_id`, so most columns need no `@Column`.
- Repository verbs: `findX`, `existsX`, `save`, `deleteById`. Service verbs: `addX`, `getXBy...`, `updateX`, `deleteX`.
- Javadoc on every public class and method: what it does, `@param`, `@return`, `@throws`.

## 10. OOP principles applied

- **Encapsulation:** state is private; the only way in is through validated methods.
- **Abstraction:** a service calls `studentService.getStudentByAm(...)` without knowing where the data comes from. The repository interface hides the whole persistence mechanism.
- **Composition over inheritance:** services hold other services. Inheritance only where there is a real "is-a" (`Student` and `Professor` extend `Person`; domain exceptions extend `RegistryException`).
- **Polymorphism:** one `@ExceptionHandler` method receives any subclass of the exception it names.

## 11. Clean code principles and patterns

- **DRY:** one `StudentCourseId` for two entities; one exception handler for the whole API.
- **Fail fast:** `ddl-auto=validate`, validation before any write, existence checks before changes.
- **Single Responsibility:** each class and each method has one job.
- **Dependency injection (Inversion of Control):** Spring owns object creation and calls our code.
- **Least privilege:** the app connects as a dedicated database user limited to one database.
- **Defense in depth:** rules are enforced in the DTOs, the model, the services and the database (foreign keys with `ON DELETE RESTRICT`, `CHECK` constraints, primary keys).
- **Boundaries with translators:** Tomcat (HTTP), Jackson (JSON and objects), Hibernate (objects and SQL rows), the JDBC driver (SQL and the wire protocol).

## 12. Database schema

Defined in `docs/schema.sql`: `students`, `professors`, `courses`, `student_courses`, `professor_courses`, `grades`.

- Composite primary keys on the three link tables.
- `ON DELETE RESTRICT` on every foreign key, a database-level mirror of the safe-delete methods.
- `CHECK (grade_value >= 0 AND grade_value <= 10)` mirrors `InvalidGradeException`.
- `utf8mb4` charset, because the data contains Greek text.
- A grade requires an enrollment: enforced in the service layer (`recordGrade`, `unenrollStudent`). A composite foreign key from `grades` to `student_courses` would enforce it in the database as well.

## 13. How a request flows

A request arrives at Tomcat. Spring MVC matches the URL and verb to a controller method, and Jackson turns the JSON body into a DTO. The controller converts the DTO into an entity and calls the service, which applies the business rules and uses the repository. The result goes back up: entity to response DTO, DTO to JSON, plus a status code. If anything throws, Spring hands the exception to the global handler, which turns it into a status code and an error body.

## 14. What Spring replaced

| Written by hand in v0.3 | Spring version |
|---|---|
| `Main` building the object graph | The Spring container and constructor injection |
| `DataSourceFactory`, `db.properties` loading, HikariCP code | `application.properties` and auto-configuration |
| Five `Jdbc*DAO` classes, SQL strings, `mapRow` | Repository interfaces and Hibernate |
| `try`/`catch SQLException` wrapped in our own exception | Spring's `DataAccessException` hierarchy |
| `ConsoleInput` parsing | Jackson, `@PathVariable`, `@RequestBody` |
| `BaseMenu.run()` catch blocks | `@RestControllerAdvice` |
| Console menus | Controllers and DTOs |

## 15. Known trade-offs and what is not done

- Each repository call is its own transaction, so "check then save" in the services is not atomic. The primary keys are the safety net. `@Transactional` is a possible next step.
- Entities carry JPA annotations directly, so the domain model is not separate from the persistence model.
- No pagination: list endpoints return every row.
- No authentication or authorization yet (planned with Spring Security).
- Automated tests are incomplete: the services other than `StudentService` and the controllers still need them.
- DTO mapping is manual, and loops are used instead of streams (deliberate while learning).
- `NoGradesRecordedException` answering 404 is a judgment call.