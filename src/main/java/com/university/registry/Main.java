package com.university.registry;

import com.university.registry.dao.*;
import com.university.registry.service.*;
import com.university.registry.ui.*;
import com.university.registry.util.DataSourceFactory;

/**
 * The Main class is the program's primary class.
 * <p>
 * It instantiates each Service following the correct dependency order.
 * It performs a series of operations to verify the proper behavior of the object graph—including entity creation,
 * registration, grade entry, and exception handling.
 */
public class Main
{
    /**
     * Application entry point.
     * <p>
     * It confirms the project skeleton builds and runs and
     * bootstraps the database connection.
     */
    private final DataSourceFactory dataSourceFactory;
    private final ConsoleInput consoleInput;
    private final MainMenu mainMenu;

    public Main()
    {
        this.dataSourceFactory = new DataSourceFactory();
        this.consoleInput = new ConsoleInput();

        StudentDAO studentDAO = new JdbcStudentDAO(dataSourceFactory); // polymorphism
        ProfessorDAO professorDAO = new ProfessorDAO(dataSourceFactory);
        CourseDAO courseDAO = new CourseDAO(dataSourceFactory);
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO(dataSourceFactory, courseDAO);
        GradeDAO gradeDAO = new GradeDAO(dataSourceFactory);

        StudentService studentService = new StudentService(studentDAO);
        ProfessorService professorService = new ProfessorService(professorDAO);
        CourseService courseService = new CourseService(courseDAO);
        EnrollmentService enrollmentService = new EnrollmentService(studentService, professorService, courseService, enrollmentDAO);
        GradeService gradeService = new GradeService(studentService, courseService, enrollmentService, gradeDAO);

        StudentMenu studentMenu = new StudentMenu(consoleInput, studentService, enrollmentService);
        ProfessorMenu professorMenu = new ProfessorMenu(consoleInput, professorService, enrollmentService);
        CourseMenu courseMenu = new CourseMenu(consoleInput, courseService, enrollmentService);
        EnrollmentMenu enrollmentMenu = new EnrollmentMenu(consoleInput, enrollmentService);
        GradeMenu gradeMenu = new GradeMenu(consoleInput, gradeService);

        this.mainMenu = new MainMenu(consoleInput, studentMenu, professorMenu, courseMenu, enrollmentMenu, gradeMenu);
    }

    /** Starts the console UI; returns when the user chooses Exit. */
    public void run()
    {
        mainMenu.run();
    }

    /** Releases the resources that live as long as the program: the input and the pool. */
    private void closeDataSource()
    {
        consoleInput.close();
        dataSourceFactory.close();
    }

    /**
     * Starts the application. If it cannot even be built (missing
     * {@code db.properties}, database down), prints a short explanation instead
     * of only a raw stack trace.
     *
     * @param args not used
     */
    public static void main(String[] args)
    {
        Main app;

        try
        {
            app = new Main();
        }
        catch (RuntimeException ex)
        {
            System.out.println("Η εφαρμογή δεν μπόρεσε να ξεκινήσει. "
                    + "Ελέγξτε το αρχείο db.properties και ότι η βάση δεδομένων λειτουργεί.");
            ex.printStackTrace();
            return;
        }

        try
        {
            app.run();
        }
        finally
        {
            app.closeDataSource();
        }
    }
}
