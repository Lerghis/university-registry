package com.university.registry.ui;

import com.university.registry.exception.DataAccessException;
import com.university.registry.exception.RegistryException;

/**
 * Abstract template for every menu of the console UI.
 * <p>
 * Implements the <b>Template Method</b> design pattern: this class owns the
 * skeleton of the algorithm (show the options, read a choice, act on it,
 * handle errors, repeat) in {@link #run()}, and each subclass supplies only
 * the steps that differ: {@link #showOptions()} and {@link #handle(int)}.
 * <p>
 * It has no options of its own and is never instantiated directly. The
 * concrete menus are {@link MainMenu}, which routes to the others, and the
 * topic menus {@link StudentMenu}, {@link ProfessorMenu}, {@link CourseMenu},
 * {@link EnrollmentMenu} and {@link GradeMenu}.
 */
public abstract class BaseMenu
{
    /** The single keyboard helper shared by every menu. */
    protected final ConsoleInput input;

    /**
     * Protected on purpose: only subclasses may call it, through {@code super(input)}.
     *
     * @param input the shared keyboard helper, created once in {@code Main}
     */
    protected BaseMenu(ConsoleInput input)
    {
        this.input = input;
    }

    /**
     * Runs this menu until the user chooses 0.
     * <p>
     * Each pass shows this menu's options, reads a choice and passes it to
     * {@link #handle(int)}. Errors raised while handling a choice are caught
     * here, once, for every menu:
     * <ul>
     *   <li>{@link RegistryException}: a business-rule violation. Its message
     *       is written for the user and is shown as it is.</li>
     *   <li>{@link DataAccessException}: a database failure. The user sees a
     *       generic message; the details go to the error stream.</li>
     *   <li>any other {@link RuntimeException}: a bug. It is reported the same
     *       way, so one failing action cannot end the whole program.</li>
     * </ul>
     * A sub-menu is started by calling its {@code run()} from the parent
     * menu's {@code handle}, so errors inside a sub-menu are caught by the
     * sub-menu's own loop and never reach the parent's.
     * <p>
     * Declared {@code final} so that no subclass can change the skeleton.
     */
    public final void run()
    {
        boolean running = true;

        while (running)
        {
            showOptions();
            int choice = input.readInt("Επιλογή: ");

            if (choice == 0)
            {
                running = false;
            }
            else
                try
                {
                    handle(choice);
                }
                catch (RegistryException ex)
                {
                    System.out.println(ex.getMessage());
                }
                catch (DataAccessException ex) // must come before RuntimeException: it is a child of it
                {
                    System.out.println("Σφάλμα βάσης δεδομένων. Δοκιμάστε ξανά.");
                    ex.printStackTrace(); // logging; prints the full cause chain
                }
                catch (RuntimeException ex)
                {
                    System.out.println("Απρόσμενο σφάλμα.");
                    ex.printStackTrace();
                }
        }
    }

    /**
     * Prints this menu's title and options, including the line for choice 0.
     * <p>
     * Abstract: each subclass supplies its own text. When {@link #run()} calls
     * this method, Java runs the version belonging to the object it is
     * executing inside (polymorphism by method overriding).
     */
    protected abstract void showOptions();

    /**
     * Carries out one choice other than 0.
     * <p>
     * Declared with {@code throws RegistryException} so that any override may
     * let a business-rule exception travel up to {@link #run()}, which shows
     * its message.
     *
     * @param choice the option the user typed; never 0, which {@link #run()}
     *               handles itself
     * @throws RegistryException if the chosen action violates a business rule
     */
    protected abstract void handle(int choice) throws RegistryException;
}
