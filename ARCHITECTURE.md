# Architecture & Design Notes

Internal reference documenting the *why* behind this project's structure —
written for myself, to actually understand the codebase rather than just
having written it.

## Why layered architecture

The original CLI prototype had one class (`DataBase`) doing five jobs at
once: repository, DAO, service, controller, and validator (a "God Object").
This made it impossible to test business logic without a keyboard attached,
or swap storage without touching UI code. Splitting into layers means each
package has exactly one reason to change (Single Responsibility Principle).

## Layer responsibilities

- **model** — plain data + self-validation only (constructors/setters throw
  exceptions on invalid state). No SQL, no I/O, no business rules about
  *other* entities.
- **exception** — `RegistryException` (abstract) + typed subclasses
  (`DuplicateEntityException`, `EntityNotFoundException`,
  `InvalidSemesterException`, `InvalidGradeException`,
  `NoGradesRecordedException`, `EntityInUseException`). Abstract base
  prevents throwing a vague, untyped error.
- **service** — business rules, in-memory storage (soon: DAO calls),
  duplicate/not-found checks, cross-entity coordination (e.g.
  `EnrollmentService` verifying both a student and course exist before
  creating an enrollment record).
- **dao** — (in progress) JDBC/SQL persistence, one class per entity.
  Services will call DAOs instead of touching a HashMap directly.
- **ui** — console menu now; will become REST controllers after the
  Spring Boot conversion. Never contains business logic, only
  input/output and delegating to services.
- **util** — `DataSourceFactory`: single shared HikariCP connection pool.

## OOP principles applied

- **Encapsulation** — every service's storage (`Map`/`List`) is `private`;
  the only way in is through validated public methods.
- **Abstraction** — callers depend on a method's contract ("throws if
  missing"), never its internal implementation (HashMap vs. eventual SQL).
- **Composition over inheritance** — `EnrollmentService` and `GradeService`
  *hold references to* other services (constructor injection) rather than
  extending them. An `EnrollmentService` isn't a kind of `StudentService`;
  it *uses* one.
- **Polymorphism (subtype)** — `catch (RegistryException ex)` can catch any
  of its subclasses, since each "is a" RegistryException. Used broadly in
  happy-path code (any failure = something unexpectedly broke) and narrowly
  in tests (confirming exactly the right exception fired).

## Clean code principles applied

- **DRY** — e.g. `GradeService.calculateAverage()` is a shared private
  helper behind `getStudentAverage`/`getCourseAverage`, instead of
  duplicating the sum/count loop twice.
- **Fail fast** — services check preconditions (entity exists, no
  duplicate) *before* any mutation, in a fixed order.
- **Acyclic Dependencies Principle** — dependency arrows only point one
  way (`EnrollmentService -> StudentService/CourseService/ProfessorService`).
  This is *why* "safe delete" logic (checking for active enrollments before
  deleting a course/student) lives in `EnrollmentService`, not in
  `CourseService`/`StudentService` — putting it there would create a
  circular dependency that can't even be constructed in Java.
- **Least privilege** — the app connects to MariaDB as a dedicated
  `registry_app` user with grants scoped to one database only, never as
  `root`.
- **Defense in depth** — validation exists at both the application layer
  (service checks, model constructors) *and* the database layer (foreign
  keys with `ON DELETE RESTRICT`, `CHECK` constraints). Neither layer
  alone is trusted exclusively.

## Database schema

See `docs/schema.sql` (or: table definitions, once added) for the full
`CREATE TABLE` statements. Key decisions:
- Composite primary keys on join tables (`student_courses`,
  `professor_courses`, `grades`) — the DB-level equivalent of the
  `studentAM + "-" + courseId` composite key used in `GradeService`.
- `ON DELETE RESTRICT` on all foreign keys — database-enforced mirror of
  `EnrollmentService.deleteStudentSafely()` / `deleteCourseSafely()`.
- `DECIMAL(4,2)` for grades, not `FLOAT` — exact decimal storage, no
  floating-point rounding error.

## Known deliberate trade-offs / not yet done

- No `equals()`/`hashCode()` overrides on model classes yet — not needed
  since HashMaps are keyed by `String` IDs, not by the objects themselves.
- Manual loops instead of Java Streams throughout the service layer —
  deliberate choice while learning; will likely refactor once comfortable.
- Checked exceptions (`extends Exception`) will become unchecked
  (`extends RuntimeException`) when converting to Spring Boot, since
  checked exceptions are awkward with Spring's exception-handling model.