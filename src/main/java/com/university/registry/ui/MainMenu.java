package com.university.registry.ui;

import com.university.registry.exception.RegistryException;

public class MainMenu extends BaseMenu
{
    private final StudentMenu studentMenu;
    private final ProfessorMenu professorMenu;

    public MainMenu(ConsoleInput input, StudentMenu studentMenu, ProfessorMenu professorMenu)
    {
        super(input);
        this.studentMenu = studentMenu;
        this.professorMenu = professorMenu;
    }

    @Override
    protected void showOptions()
    {
        System.out.println ("\n\n           Μ Ε Ν Ο Υ");
        System.out.println ("=====================================\n");
        System.out.println ("[1]......ΔΙΑΧΕΙΡΙΣΗ ΦΟΙΤΗΤΩΝ\n");
        System.out.println ("[2]......ΔΙΑΧΕΙΡΙΣΗ ΚΑΘΗΓΗΤΩΝ\n");
        System.out.println ("[3]......ΔΙΑΧΕΙΡΙΣΗ ΜΑΘΗΜΑΤΩΝ\n");
        System.out.println ("[4]......ΔΙΑΧΕΙΡΙΣΗ ΕΓΓΡΑΦΩΝ\n");
        System.out.println ("[5]......ΔΙΑΧΕΙΡΙΣΗ ΒΑΘΜΩΝ\n");
        System.out.println ("[0]......ΕΠΙΣΤΡΟΦΗ\n");
        System.out.println ("=======================================");
    }

    @Override
    protected void handle(int choice) throws RegistryException
    {

        switch (choice)
        {
            case 1 -> studentMenu.run();
            case 2 -> professorMenu.run();
            default -> System.out.println("Μη έγκυρη επιλογή.");
        }
    }
}
