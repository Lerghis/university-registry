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

    /**
     * ===================================================
     * ΕΠΕΞΗΓΗΣΗ ΤΗΣ ΙΕΡΑΡΧΙΑΣ ΤΩΝ ΕΞΑΙΡΕΣΕΩΝ ΚΑΙ ΤΩΝ CATCH BLOCKS
     * ===================================================
     * <p>
     * Κάθε μία από τις παρακάτω εξαιρέσεις (InvalidSemesterException, DuplicateEntityException,
     * EntityNotFoundException, InvalidGradeException, NoGradesRecordedException,
     * EntityInUseException) κάνει EXTEND τη RegistryException.
     * Αυτό σημαίνει ότι κάθε μία από αυτές είναι ταυτόχρονα και RegistryException (inheritance).
     * <p>
     * Εξαιτίας αυτού, το catch (RegistryException ex) μπορεί να κάνει catch οποιαδήποτε από τις Exception subclasses.
     * H Java ελέγχει το είδους του object κατά το Runtime και εφόσον το είδος του object είναι πάντα RegistryException
     * ή κάποιο από τα "παιδιά" του, το catch ταιριάζει. Αυτό είναι ο πολυμορφισμός: ένα catch block, πολλοί πιθανοί τύποι
     * εξαιρέσεων από κάτω.
     * <p>
     * Καμία από τις παρακάτω service/model μεθόδους δεν περικλύζεται από το δικό της try/catch block.
     * Όταν κάτι κάνει throw από μέσα, π.χ. Aπό το Student's Constructor ή από StudentService.addStudent, η εξαίρεση
     * συνεχίζει να ταξιδεύει προς τα πάνω μέσα από κάθε μέθοδο που την κάλεσε μέχρι να φτάσει το πρώτο try/catch block
     * που να δέχεται το είδος της. Γι'αυτό η υπογραφή κάθε μεθόδου έχει "throws…", διότι έτσι η Java αναγκάζει την εξαίρεση
     * να ταξιδέψει από όλο το path.
     */
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
