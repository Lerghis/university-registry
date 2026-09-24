# University Registry System

A layered CRUD application for managing students, professors, and courses,
built as a learning project moving from a CSV-based CLI prototype toward a
JavaFX + MySQL desktop application.

## Project structure

```
registry/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/university/registry/
│   │   │   ├── Main.java        Application entry point
│   │   │   ├── model/            Plain data classes (Student, Course, ...)
│   │   │   ├── exception/        Domain exception hierarchy
│   │   │   ├── dao/              JDBC/SQL persistence (one class per entity)
│   │   │   ├── service/          Business rules, orchestrates DAOs
│   │   │   ├── ui/               JavaFX controllers
│   │   │   └── util/             Shared helpers (e.g. DB connection factory)
│   │   └── resources/
│   │       ├── fxml/             JavaFX layout files
│   │       └── css/              Stylesheets for the JavaFX UI
│   └── test/
│       └── java/com/university/registry/   Mirrors src/main, same packages
```

## How to open this in IntelliJ

1. `File -> Open`, select the `registry/` folder (the one containing `pom.xml`)
2. IntelliJ will detect it as a Maven project automatically and import it
3. Confirm the Project SDK is set to Java 21 (`File -> Project Structure -> Project`)
4. Run `Main.java` - you should see a confirmation message printed

## Status

- [x] Layered package structure
- [x] Exception hierarchy (`RegistryException` and subclasses)
- [ ] Model classes migrated from the CSV prototype
- [ ] Service layer (business rules extracted from the old `DataBase` class)
- [ ] DAO layer + MySQL schema
- [ ] JavaFX UI
- [ ] Login / role-based access
