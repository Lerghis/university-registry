package com.university.registry.ui;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityInUseException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;
import com.university.registry.service.EnrollmentService;

import java.util.Collection;

public class EnrollmentMenu extends BaseMenu
{
    private final EnrollmentService enrollmentService;

    public EnrollmentMenu(ConsoleInput input, EnrollmentService enrollmentService)
    {
        super(input);
        this.enrollmentService = enrollmentService;
    }

    @Override
    protected void showOptions()
    {
        System.out.println ("\n\n           ΔΙΑΧΕΙΡΙΣΗ ΕΓΓΡΑΦΩΝ");
        System.out.println ("=====================================\n");
        System.out.println ("[1]......ΕΓΓΡΑΦΗ ΦΟΙΤΗΤΗ ΣΕ ΜΑΘΗΜΑ\n");
        System.out.println ("[2]......ΑΠΕΓΓΡΑΦΗ ΦΟΙΤΗΤΗ\n");
        System.out.println ("[3]......ΑΝΑΘΕΣΗ ΜΑΘΗΜΑΤΟΣ ΣΕ ΚΑΘΗΓΗΤΗ\n");
        System.out.println ("[4]......ΑΦΑΙΡΕΣΗ ΑΝΑΘΕΣΗΣ\n");
        System.out.println ("[5]......ΕΜΦΑΝΙΣΗ ΜΑΘΗΜΑΤΩΝ ΦΟΙΤΗΤΗ\n");
        System.out.println ("[6]......ΕΜΦΑΝΙΣΗ ΜΑΘΗΜΑΤΩΝ ΚΑΘΗΓΗΤΗ\n");
        System.out.println("[0]......ΕΠΙΣΤΡΟΦΗ\n");
        System.out.println ("=======================================");
    }

    @Override
    protected void handle(int choice) throws DuplicateEntityException, EntityNotFoundException, EntityInUseException
    {
        switch (choice)
        {
            case 1 -> enrollStudent();
            case 2 -> unenrollStudent();
            case 3 -> assignProfessor();
            case 4 -> unassignProfessor();
            case 5 -> showStudentCourses();
            case 6 -> showProfessorCourses();
            default -> System.out.println("Μη έγκυρη επιλογή.");
        }
    }

    private void enrollStudent() throws DuplicateEntityException, EntityNotFoundException
    {
        String studentAm = input.readString("Αριθμός μητρώου φοιτητή: ");
        String courseId = input.readString("Κωδικός μαθήματος: ");

        enrollmentService.enrollStudentToCourse(studentAm, courseId);
        System.out.println("Ο φοιτητής εγγράφηκε επιτυχώς.");
    }

    private void unenrollStudent() throws EntityInUseException, EntityNotFoundException
    {
        String studentAm = input.readString("Αριθμός μητρώου φοιτητή: ");
        String courseId = input.readString("Κωδικός μαθήματος: ");

        enrollmentService.unenrollStudent(studentAm, courseId);
        System.out.println("Επιτυχής απεγγραφή φοιτητή.");
    }

    private void assignProfessor() throws DuplicateEntityException, EntityNotFoundException
    {
        String profId = input.readString("Κωδικός καθηγητή: ");
        String courseId = input.readString("Κωδικός μαθήματος: ");

        enrollmentService.assignCourseToProfessor(profId, courseId);
        System.out.println("Επιτυχής ανάθεση μαθήματος σε καθηγητή.");
    }

    private void unassignProfessor() throws EntityNotFoundException
    {
        String profId = input.readString("Κωδικός καθηγητή: ");
        String courseId = input.readString("Κωδικός μαθήματος: ");

        enrollmentService.unassignProfessor(profId, courseId);
        System.out.println("Επιτυχής αφαίρεση ανάθεσης.");
    }

    private void showStudentCourses() throws EntityNotFoundException
    {
        String studentAm = input.readString("Εισάγετε τον αριθμό μητρώου του φοιτητή: ");
        Collection<Course> list = enrollmentService.getCoursesForStudent(studentAm);

        if (list.isEmpty())
        {
            System.out.println("Δεν βρέθηκαν μαθήματα για τον φοιτητή: " + studentAm);
        }
        else
        {
            for (Course course : list)
            {
                System.out.println(course.toString());
            }
        }
    }

    private void showProfessorCourses() throws EntityNotFoundException
    {
        String profId = input.readString("Εισάγετε τον κωδικό του καθηγητή: ");
        Collection<Course> list = enrollmentService.getCoursesForProfessor(profId);

        if (list.isEmpty())
        {
            System.out.println("Δεν βρέθηκαν μαθήματα για τον καθηγητή: " + profId);
        }
        else
        {
            for (Course course : list)
            {
                System.out.println(course.toString());
            }
        }
    }
}
