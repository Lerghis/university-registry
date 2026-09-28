# University Registry System

Μία layered CRUD εφαρμογή, σύστημα φοιτητικού μητρώου, που επιτρέπει τη διαχείριση 
μελών (φοιτητών και καθηγητών), μαθημάτων, εγγραφών και βαθμών σε ένα εκπαιδευτικό ίδρυμα.
Είναι ένα learning project το οποίο προς το παρόν είναι μια εφαρμογή κονσόλας που υποστηρίζεται
από τη Βάση Δεδομένων MariaDB, με στόχο τη μετάβαση σε REST API με Spring Boot.

## Tech stack

- Java 21
- Maven
- MariaDB (MySQL-compatible)
- JDBC + HikariCP connection pooling
- JUnit 5

## Architecture

Layered architecture, one package per layer:

com.university.registry
├── model Plain data classes (Student, Professor, Course, Grade, ...)
├── exception Domain exception hierarchy (RegistryException and subclasses)
├── dao JDBC/SQL persistence, one class per entity
├── service Business rules, orchestrates DAOs
├── ui Console controller (will become REST controllers under Spring Boot)
└── util Shared helpers (DataSourceFactory / HikariCP config)

See [ARCHITECTURE.md](ARCHITECTURE.md) for a detailed breakdown of the design
decisions, patterns, and principles applied throughout.

## Running it

1. Install MariaDB and create a `university_registry` database (see
   `ARCHITECTURE.md` for the full schema)
2. Copy `src/main/resources/db.properties.example` to `db.properties` and
   fill in your database credentials
3. Open the project in IntelliJ (`File -> Open`, select the folder containing
   `pom.xml`) — it will be detected as a Maven project automatically
4. Confirm the Project SDK is Java 21
5. Run `Main.java`

## Status

- [x] Layered package structure
- [x] Exception hierarchy
- [x] Model layer
- [x] Service layer (Student, Professor, Course, Enrollment, Grade)
- [x] MariaDB schema
- [ ] DAO layer (JDBC + HikariCP) — in progress
- [ ] Console UI layer
- [ ] Convert to Spring Boot REST API
- [ ] Spring Security login / role-based access
