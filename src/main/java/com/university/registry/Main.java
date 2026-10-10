package com.university.registry;

/**
 * Application entry point.
 * <p>
 * For now this just confirms the project skeleton builds and runs. Once the
 * service and DAO layers exist, this will bootstrap the database connection.
 */

import com.university.registry.dao.*;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.RegistryException;
import com.university.registry.model.Course;
import com.university.registry.model.Professor;
import com.university.registry.model.Student;
import com.university.registry.service.*;
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

    public Main()
    {
        this.dataSourceFactory = new DataSourceFactory();

        this.professorDAO = new ProfessorDAO(dataSourceFactory);
        this.courseDAO = new CourseDAO(dataSourceFactory);
        this.studentDAO = new StudentDAO(dataSourceFactory);
        this.enrollmentDAO = new EnrollmentDAO(dataSourceFactory, courseDAO);
        this.gradeDAO = new GradeDAO(dataSourceFactory);

        this.studentService = new StudentService(studentDAO);
        this.professorService = new ProfessorService(professorDAO);
        this.courseService = new CourseService(courseDAO);
        this.enrollmentService = new EnrollmentService(studentService, professorService, courseService, enrollmentDAO);
        this.gradeService = new GradeService(studentService, courseService, enrollmentService);

        keyboard = new Scanner(System.in);
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

        // --- FAILURE TESTS: each block DELIBERATELY tries to break one rule. ---
        // Here we catch NARROWLY (the specific subclass) on purpose: we're
        // not just checking "did something go wrong" — we're checking
        // "did EXACTLY the right kind of thing go wrong." If the exception
        // that fires is a DIFFERENT type than expected, Java won't even let
        // it into this catch block, which itself would be a useful signal
        // that our validation logic has a bug.

        try
        {
            Student student = new Student("1", 1, "testStudent1", "testEmail1", "65836758493");
            studentService.addStudent(student);
        }
        catch (RegistryException ex)
        {
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
        }


        try
        {
            Student updatedStudent = new Student("1", 1, "UpdatedTestStudent", "testEmail1", "65836758493");
            studentService.updateStudent(updatedStudent);
        }
        catch (RegistryException ex)
        {
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
        }

        try
        {
            System.out.println(studentService.getStudentByAm("1"));
        }
        catch (RegistryException ex)
        {
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
        }
        System.out.println(studentService.getAllStudents());

//        try
//        {
//            studentService.deleteStudent("1");
//        }
//        catch (RegistryException ex)
//        {
//            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
//        }

        System.out.println("\n**************************************************************\n");

        try
        {
            Professor professor = new Professor("1", "something", "someName", "someEmail", "1234567899");
            professorService.addProfessor(professor);
        }
        catch (RegistryException ex)
        {
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
        }

        try
        {
            Professor updatedProfessor = new Professor("1", "somethingElse", "someName", "someEmail", "1234567899");
            professorService.updateProfessor(updatedProfessor);
        }
        catch (RegistryException ex)
        {
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
        }

        try
        {
            System.out.println(professorService.getProfById("1"));
        }
        catch (RegistryException ex)
        {
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
        }

        System.out.println(professorService.getAllProfessors());

//        try
//        {
//            professorService.deleteProfessor("1");
//        }
//        catch (RegistryException ex)
//        {
//            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
//        }

        System.out.println("\n**************************************************************\n");

        try
        {
            Course course = new Course("2", "courseTitle2", 1);
            courseService.addCourse(course);
        }
        catch (RegistryException ex)
        {
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
        }

        try
        {
            Course updatedCourse = new Course("1", "OthercourseTitle", 1);
            courseService.updateCourse(updatedCourse);
        }
        catch (RegistryException ex)
        {
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
        }

        try
        {
            System.out.println(courseService.getCourseById("1"));
        }
        catch (RegistryException ex)
        {
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
        }

        System.out.println(courseService.getAllCourses());

//        try
//        {
//            courseService.deleteCourse("1");
//        }
//        catch (RegistryException ex)
//        {
//            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
//        }

        System.out.println("\n**************************************************************");
        System.out.println("**************************************************************");
        System.out.println("**************************************************************\n");

        try
        {
            enrollmentService.assignCourseToStudent("1", "2");
            System.out.println("ERROR: expected a DuplicateEntityException, but none was thrown!");
        }
        catch (DuplicateEntityException ex)
        {
            System.out.println("Correctly caught: " + ex.getMessage());
        }
        catch (EntityNotFoundException ex)
        {
            System.out.println("ERROR: wrong exception type: " + ex.getMessage());
        }

        try
        {
            enrollmentService.deleteCourseSafely("1");
        }
        catch (RegistryException ex)
        {
            System.out.println("Correctly caught: " + ex.getMessage());
        }

        try
        {
            System.out.println(enrollmentService.getCoursesForStudent("1"));
        }
        catch (RegistryException ex)
        {
            System.out.println("Correctly caught: " + ex.getMessage());
        }
    }

    private void closeDataSource()
    {
        dataSourceFactory.close();
    }
    public static void main(String[] args)
    {
        System.out.println("University Registry System - project skeleton is wired up correctly.");
        Main app = new Main();

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
