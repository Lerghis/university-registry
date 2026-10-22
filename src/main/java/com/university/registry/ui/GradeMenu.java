package com.university.registry.ui;

import com.university.registry.exception.*;
import com.university.registry.model.Grade;
import com.university.registry.service.GradeService;

import java.util.Collection;

/**
 * Console menu for grades: record, list a student's grades, student and course
 * averages, update and delete. When a new grade is out of range it asks again
 * (a narrow catch of {@link InvalidGradeException}) instead of returning to
 * the menu. Contains no business rules; see {@link StudentMenu}.
 */
public class GradeMenu extends BaseMenu
{
    private final GradeService gradeService;

    public GradeMenu(ConsoleInput input, GradeService gradeService)
    {
        super(input);
        this.gradeService = gradeService;
    }

    @Override
    protected void showOptions()
    {
        System.out.println ("\n\n           ΔΙΑΧΕΙΡΙΣΗ ΒΑΘΜΩΝ");
        System.out.println ("=====================================\n");
        System.out.println ("[1]......ΚΑΤΑΧΩΡΗΣΗ ΒΑΘΜΟΥ\n");
        System.out.println ("[2]......ΕΜΦΑΝΙΣΗ ΒΑΘΜΩΝ ΦΟΙΤΗΤΗ\n");
        System.out.println ("[3]......ΜΕΣΟΣ ΟΡΟΣ ΦΟΙΤΗΤΗ\n");
        System.out.println ("[4]......ΜΕΣΟΣ ΟΡΟΣ ΜΑΘΗΜΑΤΟΣ\n");
        System.out.println ("[5]......ΕΝΗΜΕΡΩΣΗ ΒΑΘΜΟΥ\n");
        System.out.println ("[6]......ΔΙΑΓΡΑΦΗ ΒΑΘΜΟΥ\n");
        System.out.println("[0]......ΕΠΙΣΤΡΟΦΗ\n");
        System.out.println ("=======================================");
    }

    @Override
    protected void handle(int choice) throws RegistryException
    {
        switch (choice)
        {
            case 1 -> recordGrade();
            case 2 -> listGradesForStudent();
            case 3 -> studentAverage();
            case 4 -> courseAverage();
            case 5 -> updateGrade();
            case 6 -> deleteGrade();
            default -> System.out.println("Μη έγκυρη επιλογή.");
        }
    }

    private void recordGrade() throws DuplicateEntityException, InvalidGradeException, EntityNotFoundException
    {
        String studentAm = input.readString("Αριθμός μητρώου φοιτητή: ");
        String courseId = input.readString("Κωδικός μαθήματος: ");
        float gradeValue = input.readFloat("Εισάγετε τον βαθμό: ");

        gradeService.recordGrade(studentAm, courseId, gradeValue);
        System.out.println("Ο βαθμός καταχωρήθηκε επιτυχώς.");
    }

    private void listGradesForStudent() throws EntityNotFoundException
    {
        String studentAm = input.readString("Αριθμός μητρώου φοιτητή: ");
        Collection<Grade> list = gradeService.getGradesForStudent(studentAm);

        if (list.isEmpty())
        {
            System.out.println("Δεν υπάρχουν καταχωρημένοι βαθμοί για τον συγκεκριμένο φοιτητή.");
        }
        else
        {
            for (Grade grade : list)
            {
                System.out.println(grade.toString());
            }
        }
    }

    private void studentAverage() throws NoGradesRecordedException, EntityNotFoundException
    {
        String studentAm = input.readString("Αριθμός μητρώου φοιτητή: ");
        float avg = gradeService.getStudentAverage(studentAm);
        System.out.print("Ο μέσος όρος βαθμών για τον φοιτητή " + studentAm + " είναι: ");
        System.out.printf("%.2f%n", avg);
    }

    private void courseAverage() throws NoGradesRecordedException, EntityNotFoundException
    {
        String courseId = input.readString("Κωδικός μαθήματος: ");
        float avg = gradeService.getCourseAverage(courseId);
        System.out.print("Ο μέσος όρος βαθμών για το μάθημα " + courseId + " είναι: ");
        System.out.printf("%.2f%n", avg);
    }

    private void updateGrade() throws EntityNotFoundException
    {
        String studentAm = input.readString("Αριθμός μητρώου φοιτητή: ");
        String courseId = input.readString("Κωδικός μαθήματος: ");
        Grade grade = gradeService.getGrade(studentAm, courseId);
        System.out.println(grade);

        boolean validInput = false;
        while (!validInput)
        {
            try
            {
                grade.setGradeValue(input.readFloatOrKeep("Νέος βαθμός ", grade.getGradeValue()));
                validInput = true;
            }
            catch (InvalidGradeException ex)
            {
                System.out.println(ex.getMessage());
            }
        }

        gradeService.updateGrade(grade);
        System.out.println("Ο βαθμός ενημερώθηκε επιτυχώς.");
    }

    private void deleteGrade() throws EntityNotFoundException
    {
        String studentAm = input.readString("Αριθμός μητρώου φοιτητή: ");
        String courseId = input.readString("Κωδικός μαθήματος: ");
        Grade grade = gradeService.getGrade(studentAm, courseId);
        System.out.println(grade);

        if (input.confirm("Διαγραφή του βαθμού;"))
        {
            gradeService.deleteGrade(studentAm, courseId);
            System.out.println("Ο βαθμός διαγράφηκε επιτυχώς.");
        }
        else
        {
            System.out.println("Η διαγραφή ακυρώθηκε.");
        }
    }
}
