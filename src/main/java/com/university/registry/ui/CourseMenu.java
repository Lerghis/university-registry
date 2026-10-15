package com.university.registry.ui;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityInUseException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.InvalidSemesterException;
import com.university.registry.model.Course;
import com.university.registry.service.CourseService;
import com.university.registry.service.EnrollmentService;

import java.util.Collection;

public class CourseMenu extends BaseMenu
{
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public CourseMenu(ConsoleInput input, CourseService courseService, EnrollmentService enrollmentService)
    {
        super(input);
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @Override
    protected void showOptions()
    {
        System.out.println ("\n\n           ΔΙΑΧΕΙΡΙΣΗ ΜΑΘΗΜΑΤΩΝ");
        System.out.println ("=====================================\n");
        System.out.println ("[1]......ΕΙΣΑΓΩΓΗ\n");
        System.out.println ("[2]......ΕΥΡΕΣΗ ΜΕ ID\n");
        System.out.println ("[3]......ΕΜΦΑΝΙΣΗ ΟΛΩΝ\n");
        System.out.println ("[4]......ΕΠΕΞΕΡΓΑΣΙΑ\n");
        System.out.println ("[5]......ΔΙΑΓΡΑΦΗ\n");
        System.out.println ("[0]......ΕΠΙΣΤΡΟΦΗ\n");
        System.out.println ("=======================================");
    }

    @Override
    protected void handle(int choice) throws DuplicateEntityException, InvalidSemesterException, EntityNotFoundException, EntityInUseException
    {
        switch (choice)
        {
            case 1 -> addCourse();
            case 2 -> findCourseById();
            case 3 -> listCourses();
            case 4 -> updateCourse();
            case 5 -> deleteCourse();
            default -> System.out.println("Μη έγκυρη επιλογή.");
        }
    }

    private void addCourse() throws InvalidSemesterException, DuplicateEntityException
    {
        String courseId = input.readString("Κωδικός μαθήματος: ");
        String courseTitle = input.readString("Τίτλος μαθήματος: ");
        int courseSemester = input.readInt("Εξάμηνο μαθήματος: ");

        Course course = new Course(courseId, courseTitle, courseSemester);
        courseService.addCourse(course);
        System.out.println("Το μάθημα προστέθηκε επιτυχώς.");
    }

    private void findCourseById() throws EntityNotFoundException
    {
        String courseId = input.readString("Εισάγετε τον κωδικό του μαθήματος προς αναζήτηση: ");
        Course course = courseService.getCourseById(courseId);
        System.out.println(course.toString());
    }

    private void listCourses()
    {
        Collection<Course> list = courseService.getAllCourses();

        if (list.isEmpty())
        {
            System.out.println("Δεν υπάρχουν μαθήματα.");
        }
        else
        {
            for (Course course : list)
            {
                System.out.println(course.toString());
            }
        }
    }

    private void updateCourse() throws EntityNotFoundException, InvalidSemesterException
    {
        String courseId = input.readString("Εισάγετε τον κωδικό του μαθήματος προς επεξεργασία: ");
        Course course = courseService.getCourseById(courseId);

        course.setCourseTitle(input.readStringOrKeep("Τίτλος μαθήματος ", course.getCourseTitle()));
        course.setCourseSemester(input.readIntOrKeep("Εξάμηνο ", course.getCourseSemester()));

        courseService.updateCourse(course);
        System.out.println("Το μάθημα ενημερώθηκε επιτυχώς.");
    }

    private void deleteCourse() throws EntityNotFoundException, EntityInUseException
    {
        String courseId = input.readString("Εισάγετε τον κωδικό του μαθήματος προς διαγραφή: ");
        courseService.getCourseById(courseId);

        if (input.confirm("Διαγραφή του μαθήματος: " + courseId + "; (n/o) "))
        {
            enrollmentService.deleteCourseSafely(courseId);
            System.out.println("Το μάθημα διαγράφηκε επιτυχώς.");
        }
        else
        {
            System.out.println("Η διαγραφή ακυρώθηκε.");
        }
    }
}
