# Architecture & Design Notes

Internal reference documenting the *why* behind this project's structure.
Written for myself, to understand the codebase rather than just having
written it. Updated as each layer is added.

## 1. Why layered architecture

The original CLI prototype had one class (`DataBase`) doing five jobs at
once: repository, DAO, service, controller, and validator (a "God Object").
Business logic could not be tested without a keyboard attached, and storage
could not be swapped without touching UI code. Splitting into layers means
each package has exactly one reason to change (Single Responsibility
Principle).

```
UI (console)  ->  Service  ->  DAO  ->  MariaDB
                    |
                  Model / Exception (used by every layer)
```

Each layer talks only to the layer directly below it.

## 2. Layer responsibilities

| Package | Responsibility | Must NOT contain |
|---|---|---|
| `model` | Plain data + self-validation (constructors/setters throw on invalid state): `Person`, `Student`, `Professor`, `Course`, `Grade`, `StudentCourses`, `ProfessorCourses` | SQL, I/O, rules about *other* entities |
| `exception` | The exception hierarchy (see section 6) | Logic |
| `dao` | Translate between objects and table rows via JDBC. One class per table (`EnrollmentDAO` owns both join tables). | Business rules, input/output |
| `service` | Business rules: existence checks, duplicate checks, cross-entity coordination. Hold no data. | SQL, `Scanner`/`System.out` |
| `ui` | Console menus: read input, call services, display results and errors. (in progress) | SQL, business rules |
| `util` | `DataSourceFactory`: builds the single HikariCP connection pool from `db.properties` | Business logic |

## 3. Object graph and construction order

`Main` is the one place where objects are created and wired together
(a *composition root*). Order matters because each object needs the ones
before it:

1. `DataSourceFactory`
2. `JdbcStudentDAO`, `ProfessorDAO`, `CourseDAO`, `GradeDAO` (need only the factory)
3. `EnrollmentDAO` (factory + `CourseDAO`, to reuse row mapping)
4. `StudentService`, `ProfessorService`, `CourseService` (one DAO each)
5. `EnrollmentService` (the three entity services + `EnrollmentDAO`)
6. `GradeService` (student, course, enrollment services + `GradeDAO`)

Dependency arrows only ever point one way; there are no cycles
(Acyclic Dependencies Principle). This is also *why* the "safe delete"
methods (`deleteStudentSafely`, `deleteCourseSafely`, ...) live in
`EnrollmentService`: it already depends on all three entity services, so
putting that logic inside `CourseService` would need a `CourseService ->
EnrollmentService` arrow, closing a cycle that cannot even be constructed
in Java.

## 4. DAO vs service: facts vs decisions

The DAO reports **facts**; the service decides what they **mean**.

| DAO reports (fact) | Service decides (rule) |
|---|---|
| `findByAM` returned `null` | `null` becomes `EntityNotFoundException` |
| `isStudentEnrolled` returned `true` | on enroll, `true` becomes `DuplicateEntityException` |
| `hasAnyGradeForStudent` returned `false` | becomes `NoGradesRecordedException` |
| `hasAnyEnrollmentForStudent` returned `true` | becomes `EntityInUseException` |

A DAO never throws a business exception, and a service never writes SQL.

Precondition worth remembering: `GradeDAO.findStudentAverage` must only be
called after `hasAnyGradeForStudent`. SQL's `AVG` over zero rows yields
`NULL`, which JDBC's `getFloat` silently turns into `0.0`, indistinguishable
from a real average of zero. The service performs that check first.

## 5. Services are stateless

After the DAO refactor no service holds a `Map` or `List` of data. Services
hold only references to collaborators (DAOs and other services); the database
is the single source of truth. Consequences:

- Data survives between runs (use `docs/reset.sql` before a manual test run).
- Services are safe to share as single instances, which is also what
  Spring singleton beans will require later.
- Public method signatures stayed identical through the refactor, so the
  layers above did not change at all. This is the payoff of layering.

## 6. Exception design

```
Throwable
  `-- Exception
        |-- RegistryException (abstract, CHECKED)         domain rule violations
        |     |-- InvalidSemesterException
        |     |-- InvalidGradeException
        |     |-- DuplicateEntityException
        |     |-- EntityNotFoundException
        |     |-- EntityInUseException
        |     `-- NoGradesRecordedException
        `-- RuntimeException
              `-- DataAccessException (UNCHECKED)         infrastructure failure
```

**Checked vs unchecked.** Both kinds are only ever *thrown* at runtime. The
only difference is a compile-time rule: for a checked exception the compiler
requires every method in the chain to declare `throws` or catch it; for an
unchecked one it requires nothing.

- Checked: things a caller can sensibly react to differently (retry, show a
  message, pick another ID).
- Unchecked: broken preconditions or infrastructure failures, where forcing
  `throws` onto every method would be noise.

**`DataAccessException` is a conversion point.** It is deliberately *not*
under `RegistryException` (it is not a business rule violation). Two things
are converted into it at the DAO boundary:

1. `SQLException` (checked) is caught in every DAO method and rethrown as
   `DataAccessException`, so SQL concerns never leak upward.
2. In `mapRowToX`, an `InvalidSemesterException` while rebuilding an object
   from a row means the stored data is corrupt (every path in was already
   validated). That is not a normal case, so it fails loudly instead of
   returning `null`.

Always pass the original exception as the cause (`ex`, **not**
`ex.getCause()`) so the full chain survives in the stack trace.

**Catching.** Catch *broadly* (`catch (RegistryException e)`) when any domain
error gets the same reaction; catch *narrowly* (`catch (DuplicateEntityException e)`)
when the reaction differs or when a test must confirm exactly the right
exception fired.

## 7. Database access rules

- **Always `PreparedStatement` with `?` placeholders**, never string
  concatenation. This prevents SQL injection: the database treats a
  placeholder's content strictly as a value, never as SQL.
- **Placeholder order must match the `?` order in the SQL text**, not the
  order the model's fields are declared in. (A real bug in `StudentDAO.update`
  came from getting this wrong.)
- **try-with-resources** for `Connection`, `PreparedStatement` and `ResultSet`.
  Closing a pooled `Connection` returns it to the pool; a forgotten one
  eventually freezes the app.
- `executeUpdate()` is for `INSERT`/`UPDATE`/`DELETE`; `executeQuery()` is for
  `SELECT`. They are alternatives, never two steps of one process.
- **Avoid the N+1 query problem.** `findCoursesForStudent` uses one `JOIN`
  instead of one query for the enrollments plus one query per course.
- In joins, **list columns explicitly** (`SELECT c.course_id, ...`), because
  both tables contain a `course_id` column.
- Multi-line SQL uses Java text blocks (`"""..."""`).
- Row-to-object mapping lives in one `mapRowToX` helper per DAO (DRY).
  `CourseDAO.mapRowToCourse` is package-private so `EnrollmentDAO` can reuse
  it without exposing it outside the `dao` package.

## 8. Layering rule for services

A service talks to **DAOs** for its own entity's data, and to **other
services** for another entity's data; it never reaches into another entity's
DAO. Example: `GradeService.recordGrade` checks enrollment through
`EnrollmentService.isStudentEnrolled(...)`, not `EnrollmentDAO` directly.
Otherwise two services would become responsible for the same table.

The UI layer likewise talks only to services, never to DAOs, and must call
the *safe* delete operations in `EnrollmentService` rather than the plain
`deleteX` methods on the entity services.

## 9. Naming conventions

- **Database:** `snake_case`, plural table names (`students`, `student_courses`,
  columns `student_am`, `course_id`, `grade_value`).
- **Java:** `camelCase` fields and methods (`studentAm`, `courseId`).
- The two are translated in the DAO layer. `snake_case` was chosen over
  camelCase columns because it is the near-universal SQL convention, and
  because Spring Boot's default naming strategy maps a Java field
  `courseId` to a column `course_id` automatically, so the later JPA
  conversion needs no per-field `@Column` overrides.
- DAO verbs: `insert`, `findX`, `findAll`, `update`, `delete`.
  Service verbs: `addX`, `getXBy...`, `updateX`, `deleteX`, `getAllXs`.
- Javadoc on every public class and method: what it does, `@param`,
  `@return`, `@throws`.

## 10. OOP principles applied

- **Encapsulation:** state is private; the only way to change it is through
  validated public methods (model setters, service methods).
- **Abstraction:** `EnrollmentService` calls `studentService.getStudentByAm(...)`
  without knowing the answer now comes from MariaDB instead of a `HashMap`.
- **Composition over inheritance:** services *hold* other services; an
  `EnrollmentService` is not a kind of `StudentService`. (Inheritance is used
  where there is a real "is-a": `Student`/`Professor` extend `Person`, every
  domain exception extends `RegistryException`.)
- **Subtype polymorphism:** one `catch (RegistryException e)` receives any
  subclass; the runtime checks the real type of the thrown object.

## 11. Clean code principles and patterns applied

- **DRY:** shared `mapRowToX` helpers; existence checks reuse `getXBy...`.
- **Fail fast:** preconditions are checked before any mutation; corrupt data
  throws immediately instead of returning `null`.
- **Single Responsibility:** applies to methods as well as classes
  (`DataSourceFactory`'s constructor delegates to `loadProperties()` and
  `createDataSource()`).
- **Dependency injection:** a class receives collaborators built elsewhere
  through its constructor. Initializing a class's *own* internal state in its
  constructor is ordinary initialization, not injection.
- **Least privilege:** the app connects as `registry_app`, whose grants are
  scoped to one database, never as `root`.
- **Defense in depth:** rules are enforced both in Java (service checks, model
  validation) and in MariaDB (foreign keys with `ON DELETE RESTRICT`, `CHECK`
  constraints, composite primary keys).
- **Design patterns:** DAO/Repository, Dependency Injection, a shared
  connection pool built once and injected everywhere (singleton by
  discipline rather than by enforcement).
- **Secrets stay out of git:** `db.properties` is ignored; only
  `db.properties.example` is committed.

## 12. Database schema

Defined in `docs/schema.sql`. Key decisions:

- Composite primary keys on `student_courses`, `professor_courses` and
  `grades`: the database-level version of the old `studentAM + "-" + courseId`
  string key.
- `ON DELETE RESTRICT` on every foreign key, a database-enforced mirror of the
  `deleteXSafely` methods.
- `DECIMAL(4,2)` for grades, not `FLOAT`: exact storage, no rounding surprises.
- `CHECK (grade_value >= 0 AND grade_value <= 10)` mirrors `InvalidGradeException`.
- `utf8mb4` charset, because the data contains Greek text.
- Enrollment-to-grade integrity is currently enforced in the service layer
  (`recordGrade` requires an enrollment; `unenrollStudent` refuses when a grade
  exists). A composite foreign key from `grades` to `student_courses` would
  enforce it in the database as well.

## 13. Development workflow

- One branch per refactor/feature, merged into `main` when it works;
  small commits with imperative messages.
- Tags mark milestones: `v0.1-in-memory-services` (services backed by
  `HashMap`/`List`, kept as a reference for the manual logic) and
  `v0.2-services-use-daos` (everything backed by MariaDB).
- Data persists between runs, so reset with `docs/reset.sql` before a manual
  test run. Delete order matters (child tables first) because of the foreign keys.

## 14. Known deliberate trade-offs / not yet done

- Manual loops instead of Java Streams: deliberate while learning; revisit later.
- No `equals()`/`hashCode()` on model classes: maps are keyed by `String` IDs.
- "Check, then act" duplicate detection is fine for a single-user console app
  (the primary key is the safety net underneath); a concurrent server would
  need to handle the race explicitly.
- No logging framework yet (HikariCP prints an SLF4J "NOP logger" warning).
- Pool size is hard-coded to 10 in `DataSourceFactory`.
- `StudentCourses` and `ProfessorCourses` model classes are currently unused;
  decide their fate during the JPA conversion.
- Automated tests exist only for the exception hierarchy.
- Spring Boot conversion: `RegistryException` will likely become unchecked,
  since checked exceptions fit poorly with Spring's exception handling.

## 15. UI layer (console)

- **Template Method:** `BaseMenu.run()` owns the loop and the error handling;
  each menu supplies only `showOptions()` and `handle(int)`.
- **Hub and spoke:** `MainMenu` routes to the five topic menus; topic menus never
  know about each other or about `MainMenu`.
- **Boundary:** `ConsoleInput` turns raw typing into typed values. The UI checks
  the *format* of input; the model classes decide *validity*.
- **Error policy:** `RegistryException` shows its own (user-facing) message;
  `DataAccessException` shows a generic message and the stack trace goes to
  stderr; any other `RuntimeException` is reported the same way so the program
  keeps running.
- **Rules:** menus call only services, and always the *safe* deletes on
  `EnrollmentService`. Destructive actions use `confirm`, where anything but a
  clear yes means no.
- **Known issue:** prompts contain no lookalike letters (Latin `n` vs Greek `ν`);
  `confirm` appends its own suffix for this reason.