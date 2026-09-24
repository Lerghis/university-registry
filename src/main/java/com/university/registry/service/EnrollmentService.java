package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityInUseException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;
import com.university.registry.model.ProfessorCourses;
import com.university.registry.model.StudentCourses;

import java.util.ArrayList;
import java.util.List;

/**
 * This class uses constructor injection by calling StudentService and CourseService.
 * This service depends on other services, and it owns two collections at once (StudentCourses and ProfessorCourses) which both represent the same kind of thing -> a join record.
 */

/**
 * [OOP — Composition] Notice the constructor: EnrollmentService doesn't extend StudentService/ProfessorService/CourseService — it holds a reference to each of them as fields.
 * This is composition ("has-a") rather than inheritance ("is-a"), and it's the correct choice here: an EnrollmentService isn't a kind of StudentService, it uses one.
 * This is actually a well-known principle worth naming directly: "favor composition over inheritance" — a very real, commonly cited design guideline.
 */
public class EnrollmentService
{
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CourseService courseService;

    // we are using ArrayLists because there is no single unique key for an enrollment. A student can have many enrollments, a course can have many enrolled students.
    private final List<StudentCourses> studentEnrollments;
    private final List<ProfessorCourses> professorAssignments;

    public EnrollmentService(StudentService studentService, ProfessorService professorService, CourseService courseService)
    {
        this.studentService = studentService;
        this.professorService = professorService;
        this.courseService = courseService;

        this.studentEnrollments = new ArrayList<>();
        this.professorAssignments = new ArrayList<>();
    }

    public void assignCourseToStudent(String am, String courseId) throws EntityNotFoundException, DuplicateEntityException
    {
        studentService.getStudentByAM(am);
        courseService.getCourseById(courseId);

        boolean alreadyEnrolled = false;
        for (StudentCourses enrollment : studentEnrollments)
        {
            if (enrollment.getStudentAM().equals(am) && enrollment.getCourseId().equals(courseId))
            {
                alreadyEnrolled = true;
                break;
            }
        }

        if (alreadyEnrolled)
        {
            throw new DuplicateEntityException("Enrollment", am + "-" + courseId);
        }

        studentEnrollments.add(new StudentCourses(am, courseId));
    }

    public void assignCourseToProfessor(String profId, String courseId) throws EntityNotFoundException, DuplicateEntityException
    {
        professorService.getProfById(profId);
        courseService.getCourseById(courseId);

        boolean alreadyAssigned = false;
        for (ProfessorCourses assignment : professorAssignments)
        {
            if (assignment.getProfId().equals(profId) && assignment.getCourseId().equals(courseId))
            {
                alreadyAssigned = true;
                break;
            }
        }

        if (alreadyAssigned)
        {
            throw new DuplicateEntityException("Assignment", profId + "-" + courseId);
        }

        professorAssignments.add(new ProfessorCourses(profId, courseId));
    }

    public List<Course> getCoursesForStudent(String am) throws EntityNotFoundException
    {
        /*
        1. Περνάμε στη μέθοδο παραμετρικά ένα ΑΜ.
        2. Περνάμε το ΑΜ ως argument σε ένα άλλο Object το studentService το οποίο είναι τύπου StudentService καθώς και
            πεδίο της παρούσας κλάσης EnrollmentService. Ουσιαστικά το EnrollmentService λέει στο StudentService να ψάξει μέσα
            στο εσωτερικό του HashMap και να εξακριβώσει έαν ο φοιτητής με το συγκεκριμένο ΑΜ υπάρχει. Επιστρέφει είτε ένα Object Student,
            είτε πετάει exception.
            Το EnrollmentService δε γνωρίζει ότι υπάρχει το HashMap, το μόνο που ξέρει είναι ότι μπορεί να ρωτήσει το StudentService και να
            εμπιστευτεί την απάντηση. Αυτό είναι η αρχή της ΕΝΘΥΛΑΚΩΣΗΣ. Τα HashMap's του κάθε service είναι private και έτσι ο μόνος
            τρόπος αλληλεπίδρασης με τα δεδομένα των service classes είναι μέσω των public μεθόδων τους. Το γεγονός ότι ο εσωτερικός μηχανισμός της getStudentByAM
            που κοιτάει το HashMap είναι κρυφός, είναι σκόπιμο και αποτελεί την αρχή του ABSTRACTION.
        3. Δημιουργούμε μια νέα List που αποτελείται από Courses η οποία θα κρατάει όλα τα Courses στα οποία έχει κάνει enroll ο φοιτητής που έχει
            το ΑΜ που περνάμε ως παράμετρο στην παρούσα μέθοδο.
        4. Φτιάχνουμε μια enhanced for loop για να κάνουμε προσπέλαση μέσα στο studentEnrollments όλα τα StudentCourses που κρατάει ώστε να βρούμε
            ποια από αυτά έχουν το ίδιο ΑΜ με αυτό της παραμέτρου μας. Όσα match γίνουν, τόσα και τα Courses στα οποία έχει κάνει enroll ο φοιτητής
            με το συγκεκριμένο ΑΜ.
        5. Εάν γίνει το matching, ζητάμε από το courseService να ψάξει στο HashMap του και να βρει το Course με το συγκεκριμένο id.
            Αυτό γίνεται με το να περάσουμε παραμετρικά στη μέθοδο του courseService που κοιτάει το HashMap, τη μεταβλητή enrollment που είναι
            Object τύπου StudentCourses που σημαίνει ότι περιέχει και AM και courseId.
            Στη συνέχεια αποθηκέυουμε το Course που βρήκαμε μέσα σε μία μεταβλητή που είναι Object Course καθώς η λίστα μας περιέχει αντικείμενα Course.
        6. Προσθέτουμε το Object Course μέσα στη λίστα Courses που θέλουμε να επιστρέψουμε και η λούπα επαναλαμβάνεται για όλα τα StudentCourses που έχει
            μέσα το studentEnrollments.
        7. Τέλος, όταν η λούπα τελειώσει επιστρέφουμε τη λίστα που περιέχει όλα τα Courses του φοιτητή με το συγκεκριμένο ΑΜ που περάσαμε παραμετρικά στη μέθοδο.
         */

        studentService.getStudentByAM(am); // επιβεβαιώνουμε ότι ο φοιτητής υπάρχει

        List<Course> courses = new ArrayList<>();
        for (StudentCourses enrollment : studentEnrollments)
        {
            if (enrollment.getStudentAM().equals(am))
            {
                Course course = courseService.getCourseById(enrollment.getCourseId());
                courses.add(course);
            }
        }

        return courses;
    }

    public List<Course> getCoursesForProfessor(String profId) throws EntityNotFoundException
    {
        professorService.getProfById(profId);

        List<Course> courses = new ArrayList<>();
        for (ProfessorCourses assignment : professorAssignments)
        {
            if (assignment.getProfId().equals(profId))
            {
                Course course = courseService.getCourseById(assignment.getCourseId());
                courses.add(course);
            }
        }

        return courses;
    }

    public void deleteCourseSafely(String courseId) throws EntityNotFoundException, EntityInUseException
    {
        courseService.getCourseById(courseId);

        for (StudentCourses enrollments : studentEnrollments)
        {
            if (enrollments.getCourseId().equals(courseId))
            {
                throw new EntityInUseException("Course", courseId);
            }
        }

        for (ProfessorCourses assignments : professorAssignments)
        {
            if (assignments.getCourseId().equals(courseId))
            {
                throw new EntityInUseException("Course", courseId);
            }
        }

        courseService.deleteCourse(courseId);
    }

    public void deleteStudentSafely(String am) throws EntityNotFoundException, EntityInUseException
    {
        studentService.getStudentByAM(am);

        for (StudentCourses enrollment : studentEnrollments)
        {
            if (enrollment.getStudentAM().equals(am))
            {
                throw new EntityInUseException("Student", am);
            }
        }

        studentService.deleteStudent(am);
    }

    public void deleteProfessorSafely(String profId) throws EntityNotFoundException, EntityInUseException
    {
        professorService.getProfById(profId);

        for (ProfessorCourses assignment : professorAssignments)
        {
            if (assignment.getProfId().equals(profId))
            {
                throw new EntityInUseException("Professor", profId);
            }
        }

        professorService.deleteProfessor(profId);
    }
}
