package com.university.registry;

/**
 * Application entry point.
 * <p>
 * For now this just confirms the project skeleton builds and runs. Once the
 * service and DAO layers exist, this will bootstrap the database connection
 * and launch the JavaFX application instead.
 */

import com.university.registry.exception.*;
import com.university.registry.model.Course;
import com.university.registry.model.Student;
import com.university.registry.service.*;
import com.university.registry.util.DataSourceFactory;

import java.sql.Connection;
import java.sql.SQLException;

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
    private final DataSourceFactory dataSource;

    public Main()
    {
        this.studentService = new StudentService();
        this.professorService = new ProfessorService();
        this.courseService = new CourseService();
        this.enrollmentService = new EnrollmentService(studentService, professorService, courseService);
        this.gradeService = new GradeService(studentService, courseService, enrollmentService);
        this.dataSource = new DataSourceFactory();
    }

    /**
     * =====================================================================
     * ΕΠΕΞΗΓΗΣΗ ΤΗΣ ΙΕΡΑΡΧΙΑΣ ΤΩΝ ΕΞΑΙΡΕΣΕΩΝ ΚΑΙ ΤΩΝ CATCH BLOCKS
     * =====================================================================
     * - Κάθε μία από τις παρακάτω εξαιρέσεις (InvalidSemesterException, DuplicateEntityException,
     *   EntityNotFoundException, InvalidGradeException, NoGradesRecordedException,
     *   EntityInUseException) κάνει EXTEND τη RegistryException.
     *   Αυτό σημαίνει ότι κάθε μία από αυτές είναι ταυτόχρονα και RegistryException (inheritance).
     *
     * - Εξαιτίας αυτού, το catch (RegistryException ex) μπορεί να κάνει catch οποιαδήποτε από τις Exception subclasses.
     *   H Java ελέγχει το είδους του object κατά το Runtime και εφόσον το είδος του object είναι πάντα RegistryException
     *   ή κάποιο από τα "παιδιά" του, το catch ταιριάζει. Αυτό είναι ο πολυμορφισμός: ένα catch block, πολλοί πιθανοί τύποι
     *   εξαιρέσεων από κάτω.
     *
     * - Καμία από τις παρακάτω service/model μεθόδους δεν περικλύζεται από το δικό της try/catch block.
     *   Όταν κάτι κάνει throw από μέσα, π.χ. Aπό το Student's Constructor ή από StudentService.addStudent, η εξαίρεση
     *   συνεχίζει να ταξιδεύει προς τα πάνω μέσα από κάθε μέθοδο που την κάλεσε μέχρι να φτάσει το πρώτο try/catch block
     *   που να δέχεται το είδος της. Γι'αυτό η υπογραφή κάθε μεθόδου έχει "throws…", διότι έτσι η Java αναγκάζει την εξαίρεση
     *   να ταξιδέψει από όλο το path.
     */
    public void runSmokeTest()
    {

        try (Connection conn = dataSource.getDataSource().getConnection())
        {
            System.out.println("Database connection successful: " + conn.getCatalog());
        }
        catch (SQLException ex)
        {
            System.out.println("Database connection FAILED: " + ex.getMessage());
        }

        try
        {
            Student student = new Student("AM1001", 1, "testName", "test@gmail.com", "2435464546");
            studentService.addStudent(student); // could throw DuplicateEntityException


            Course course = new Course("CS101", "testCourse", 1);
            courseService.addCourse(course); // could throw DuplicateEntityException

            enrollmentService.assignCourseToStudent("AM1001", "CS101");
            // could throw EntityNotFoundException (student/course missing)
            // could throw DuplicateEntityException (already enrolled)

            gradeService.recordGrade("AM1001", "CS101", 8.5f);
            // could throw EntityNotFoundException (not enrolled)
            // could throw DuplicateEntityException (grade already recorded)
            // could throw InvalidGradeException (out of 0-10 range)

            float average = gradeService.getStudentAverage("AM1001");
            // could throw EntityNotFoundException or NoGradesRecordedException
            System.out.println("Student average: " + average);
        }
        catch (RegistryException ex)
        {
            // Δε χρειάζεται να ξέρουμε ποια από τις εξαιρέσεις πυροδοτήθηκε καθώς οποιαδήποτε φτάσει μέχρι εδώ σημαίνει
            // ότι κάτι μέσα στο path που δεν έπρεπε να σπάσει, έσπασε
            System.out.println("Unexpected failure in the happy path: " + ex.getMessage());
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
            Student student = new Student("AM1002", 2, "testStudent2", "testEmail2", "65836758493");
            studentService.addStudent(student);
        }
        catch (DuplicateEntityException | InvalidSemesterException ex)
        {
            System.out.println("Correctly caught: " + ex.getMessage());
        }

        try
        {
            Course course = new Course("CS102", "testCourse2", 1);
            courseService.addCourse(course);
        }
        catch (InvalidSemesterException | DuplicateEntityException ex)
        {
            System.out.println("Correctly caught: " + ex.getMessage());
        }

        try
        {
            enrollmentService.assignCourseToStudent("AM1001", "CS102");
        }
        catch (EntityNotFoundException | DuplicateEntityException ex)
        {
            System.out.println(ex.getMessage());
        }

        try
        {
            gradeService.recordGrade("AM1001", "CS102", 2.5f);
        }
        catch (EntityNotFoundException | DuplicateEntityException | InvalidGradeException ex)
        {
            System.out.println(ex.getMessage());
        }

        try
        {
            float average = gradeService.getStudentAverage("AM1001");
            System.out.println("Student average: " + average);
        }
        catch (EntityNotFoundException | NoGradesRecordedException ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    public static void main(String[] args)
    {
        System.out.println("University Registry System - project skeleton is wired up correctly.");
        Main app = new Main();
        app.runSmokeTest();
    }
}
