package com.university.registry.ui;

import com.university.registry.exception.DataAccessException;
import com.university.registry.exception.RegistryException;

import java.util.Arrays;

/**
 * abstract class Menu:
 *     protected final ConsoleInput input
 *
 *     constructor Menu(input): store it
 *
 *     public final void run():
 *         running = true
 *         while running:
 *             showOptions()
 *             choice = input.readInt("Επιλογή: ")
 *             if choice == 0:
 *                 running = false                     // 0 always means "leave this menu"
 *             else:
 *                 try:
 *                     handle(choice)
 *                 catch RegistryException ex:
 *                     print ex.getMessage()
 *                 catch DataAccessException ex:       // before RuntimeException: it is a child of it
 *                     print "Σφάλμα βάσης δεδομένων. Δοκιμάστε ξανά."
 *                     print the stack trace for yourself (a stand-in for real logging)
 *                 catch RuntimeException ex:
 *                     print "Απρόσμενο σφάλμα."
 *                     print the stack trace
 *
 *     protected abstract void showOptions()
 *     protected abstract void handle(int choice) throws RegistryException
 */
public abstract class BaseMenu
{
    protected final ConsoleInput input;

    public BaseMenu(ConsoleInput input)
    {
        this.input = input;
    }

    public final void run()
    {
        boolean running = true;

        while (running)
        {
            showOptions();
            int choice = input.readInt("Επιλογή: ");

            if (choice == 0)
                running = false;
            else
                try
                {
                    handle(choice);
                }
                catch (RegistryException ex)
                {
                    System.out.println(ex.getMessage());
                }
                catch (DataAccessException ex)
                {
                    System.out.println("Σφάλμα βάσης δεδομένων. Δοκιμάστε ξανά.");
                    System.out.println(Arrays.toString(ex.getStackTrace()));
                }
                catch (RuntimeException ex)
                {
                    System.out.println("Απρόσμενο σφάλμα.");
                    System.out.println(Arrays.toString(ex.getStackTrace()));
                }
        }
    }

    protected abstract void showOptions();

    protected abstract void handle(int choice) throws RegistryException;
}
