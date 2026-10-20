package com.university.registry;

import com.university.registry.dao.*;
import com.university.registry.service.*;
import com.university.registry.ui.*;
import com.university.registry.util.DataSourceFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Η κλάση Main είναι η βασική κλάση του προγράμματος. Κατασκευάζει κάθε Service με τη σωστή σειρά εξάρτησης.
 * Εκτελεί μία σειρά λειτουργιών για την επαλήθευση της ορθής συμπεριφοράς του γράφου αντικειμένων - δημιουργία
 * οντοτήτων, εγγραφή, καταχώρηση βαθμού και διαχείριση εξαιρέσεων.
 */
public class Main
{
    /**
     * Application entry point.
     * <p>
     * It confirms the project skeleton builds and runs and
     * bootstraps the database connection.
     */

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final DataSourceFactory dataSourceFactory;

    private final StudentDAO studentDAO;
    private final ProfessorDAO professorDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final GradeDAO gradeDAO;

    Scanner keyboard;
    private final ConsoleInput consoleInput;
    private final StudentMenu studentMenu;
    private final ProfessorMenu professorMenu;
    private final CourseMenu courseMenu;
    private final EnrollmentMenu enrollmentMenu;
    private final GradeMenu gradeMenu;

    private final MainMenu mainMenu;

    public Main(ConsoleInput consoleInput)
    {
        this.dataSourceFactory = new DataSourceFactory();

        this.studentDAO = new StudentDAO(dataSourceFactory);
        this.professorDAO = new ProfessorDAO(dataSourceFactory);
        this.courseDAO = new CourseDAO(dataSourceFactory);
        this.enrollmentDAO = new EnrollmentDAO(dataSourceFactory, courseDAO);
        this.gradeDAO = new GradeDAO(dataSourceFactory);

        this.studentService = new StudentService(studentDAO);
        this.professorService = new ProfessorService(professorDAO);
        this.courseService = new CourseService(courseDAO);
        this.enrollmentService = new EnrollmentService(studentService, professorService, courseService, enrollmentDAO);
        this.gradeService = new GradeService(studentService, courseService, enrollmentService, gradeDAO);

        this.consoleInput = consoleInput;
        this.studentMenu = new StudentMenu(consoleInput, studentService, enrollmentService);
        this.professorMenu = new ProfessorMenu(consoleInput, professorService, enrollmentService);
        this.courseMenu = new CourseMenu(consoleInput, courseService, enrollmentService);
        this.enrollmentMenu = new EnrollmentMenu(consoleInput, enrollmentService);
        this.gradeMenu = new GradeMenu(consoleInput, gradeService);
        this.mainMenu = new MainMenu(consoleInput, studentMenu, professorMenu, courseMenu, enrollmentMenu, gradeMenu);
    }

    public void runSmokeTest()
    {
        try (Connection conn = dataSourceFactory.getDataSource().getConnection())
        {
            System.out.println("Database connection successful: " + conn.getCatalog());
        }
        catch (SQLException ex)
        {
            System.out.println("Database connection FAILED: " + ex.getMessage());
        }

        mainMenu.run();
    }

    private void closeDataSource()
    {
        dataSourceFactory.close();
    }
    public static void main(String[] args)
    {
        System.out.println("University Registry System - project skeleton is wired up correctly.");
        Main app = new Main(new ConsoleInput());

        try
        {
            app.runSmokeTest();
        }
        finally
        {
            app.closeDataSource();
        }
    }
}
