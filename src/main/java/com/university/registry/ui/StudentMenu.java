package com.university.registry.ui;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityInUseException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.InvalidSemesterException;
import com.university.registry.model.Student;
import com.university.registry.service.EnrollmentService;
import com.university.registry.service.StudentService;

import java.util.Collection;

/**
 * Console menu for managing students: add, find by AM, list, update and delete.
 * <p>
 * Contains no business rules. It reads the input, delegates to
 * {@link StudentService}, and lets any RegistryException travel up to
 * {@link BaseMenu#run()}, which shows the message. Deletion goes through
 * {@link EnrollmentService#deleteStudentSafely(String)}, so a student who is
 * still enrolled cannot be removed.
 */
public class StudentMenu extends BaseMenu
{
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    /**
     *
     * @param input             the shared keyboard helper
     * @param studentService    used for add, find, list and update
     * @param enrollmentService used for the safe delete
     */
    public StudentMenu(ConsoleInput input, StudentService studentService, EnrollmentService enrollmentService)
    {
        super(input);
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }

    @Override
    protected void showOptions()
    {
        System.out.println ("\n\n           ΔΙΑΧΕΙΡΙΣΗ ΦΟΙΤΗΤΩΝ");
        System.out.println ("=====================================\n");
        System.out.println ("[1]......ΕΙΣΑΓΩΓΗ\n");
        System.out.println ("[2]......ΕΥΡΕΣΗ ΜΕ ΑΜ\n");
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
            case 1 -> addStudent();
            case 2 -> findStudent();
            case 3 -> listStudents();
            case 4 -> updateStudent();
            case 5 -> deleteStudent();
            default -> System.out.println("Μη έγκυρη επιλογή");
        }
    }

    private void addStudent() throws InvalidSemesterException, DuplicateEntityException
    {
        String am = input.readString("Αριθμός μητρώου: ");
        String name = input.readString("Ονοματεπώνυμο: ");
        int semester = input.readInt("Εξάμηνο: ");
        String email = input.readString("Email: ");
        String phone = input.readString("Τηλέφωνο: ");

        Student student = new Student(am, semester, name, email, phone);
        studentService.addStudent(student);
        System.out.println("Ο φοιτητής προστέθηκε επιτυχώς.");
    }

    private void findStudent() throws EntityNotFoundException
    {
        String am = input.readString("Εισάγετε τον αριθμό μητρώου του φοιτητή προς προβολή: ");
        Student student = studentService.getStudentByAm(am);
        System.out.println(student.toString());
    }

    private void listStudents()
    {
        Collection<Student> list = studentService.getAllStudents();

        if (list.isEmpty())
        {
            System.out.println("Δεν υπάρχουν φοιτητές.");
        }
        else
        {
            for (Student student : list)
            {
                System.out.println(student.toString());
            }
        }
    }

    private void updateStudent() throws EntityNotFoundException, InvalidSemesterException
    {
        String am = input.readString("Εισάγετε τον αριθμό μητρώου του φοιτητή προς επεξεργασία: ");
        Student student = studentService.getStudentByAm(am);
        System.out.println(student);

        student.setName(input.readStringOrKeep("Ονοματεπώνυμο ", student.getName()));
        student.setSemester(input.readIntOrKeep("Εξάμηνο ", student.getSemester()));
        student.setEmail(input.readStringOrKeep("Email ", student.getEmail()));
        student.setPhone(input.readStringOrKeep("Τηλέφωνο ", student.getPhone()));

        studentService.updateStudent(student);
        System.out.println("Τα στοιχεία του φοιτητή ενημερώθηκαν.");
    }

    private void deleteStudent() throws EntityNotFoundException, EntityInUseException
    {
        String am = input.readString("Εισάγετε τον αριθμό μητρώου του φοιτητή προς διαγραφή: ");

        if (input.confirm("Διαγραφή του φοιτητή: " + am + ";"))
        {
            enrollmentService.deleteStudentSafely(am);
            System.out.println("Ο φοιτητής διαγράφηκε επιτυχώς.");
        }
        else
        {
            System.out.println("Η διαγραφή ακυρώθηκε.");
        }
    }
}
