package com.university.registry.ui;

/**
 * The entry menu (the hub): each option calls {@code run()} on the matching
 * topic menu. Holds no services, because routing is its only job. It
 * implements {@code showOptions()} and {@code handle(int)} and inherits
 * {@code run()} and the shared {@code input} from {@link BaseMenu}.
 */
public class MainMenu extends BaseMenu
{
    private final StudentMenu studentMenu;
    private final ProfessorMenu professorMenu;
    private final CourseMenu courseMenu;
    private final EnrollmentMenu enrollmentMenu;
    private final GradeMenu gradeMenu;

    public MainMenu(ConsoleInput input, StudentMenu studentMenu, ProfessorMenu professorMenu, CourseMenu courseMenu, EnrollmentMenu enrollmentMenu, GradeMenu gradeMenu)
    {
        super(input); // it passes input up to the parent’s constructor, which stores it in the field
        this.studentMenu = studentMenu;
        this.professorMenu = professorMenu;
        this.courseMenu = courseMenu;
        this.enrollmentMenu = enrollmentMenu;
        this.gradeMenu = gradeMenu;
    }

    @Override
    protected void showOptions()
    {
        System.out.println ("\n\n           Μ Ε Ν Ο Υ");
        System.out.println ("=====================================\n");
        System.out.println ("[1]......ΔΙΑΧΕΙΡΙΣΗ ΦΟΙΤΗΤΩΝ\n");
        System.out.println ("[2]......ΔΙΑΧΕΙΡΙΣΗ ΚΑΘΗΓΗΤΩΝ\n");
        System.out.println ("[3]......ΔΙΑΧΕΙΡΙΣΗ ΜΑΘΗΜΑΤΩΝ\n");
        System.out.println ("[4]......ΔΙΑΧΕΙΡΙΣΗ ΕΓΓΡΑΦΩΝ/ΑΝΑΘΕΣΕΩΝ\n");
        System.out.println ("[5]......ΔΙΑΧΕΙΡΙΣΗ ΒΑΘΜΩΝ\n");
        System.out.println ("[0]......ΕΞΟΔΟΣ\n");
        System.out.println ("=======================================");
    }

    @Override
    protected void handle(int choice)
    {
        switch (choice)
        {
            case 1 -> studentMenu.run();
            case 2 -> professorMenu.run();
            case 3 -> courseMenu.run();
            case 4 -> enrollmentMenu.run();
            case 5 -> gradeMenu.run();
            default -> System.out.println("Μη έγκυρη επιλογή.");
        }
    }
}
