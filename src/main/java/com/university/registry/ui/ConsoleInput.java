package com.university.registry.ui;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Safe keyboard input for the console UI.
 * <p>
 * Every method reads a whole line with {@code nextLine()} and converts it
 * itself, so no newline is left in the buffer (the problem {@code nextInt()}
 * causes). Malformed input, such as an empty required value or text where a
 * number is expected, is handled here by asking again, so it never reaches the
 * rest of the program. This is the boundary where raw human typing becomes
 * typed values.
 * <p>
 * Only the format of the input is checked here. Whether a value is
 * <i>valid</i> (a semester of at least 1, a grade between 0 and 10) is decided
 * by the model classes.
 * <p>
 * One instance is created in {@code Main} and shared by every menu, because
 * two {@code Scanner}s on {@code System.in} would compete for buffered input.
 */
public class ConsoleInput
{
    private final Scanner keyboard;

    /** Creates a helper that reads the keyboard as UTF-8, so Greek input works. */
    public ConsoleInput()
    {
        keyboard = new Scanner(System.in, StandardCharsets.UTF_8);
    }

    /**
     * Asks until the user types a non-blank value.
     *
     * @param prompt the question to show; printed as it is, without a newline
     * @return the trimmed, non-empty text
     */
    public String readString(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = keyboard.nextLine().trim();

            if (!line.isEmpty()) return line;

            System.out.println("Η τιμή δεν μπορεί να είναι κενή.");
        }
    }

    /**
     * For updates: shows the current value in brackets; pressing Enter keeps it.
     *
     * @param prompt  the question to show
     * @param current the existing value, returned if the user types nothing
     * @return the new text, or {@code current} if the line was empty
     */
    public String readStringOrKeep(String prompt, String current)
    {
        System.out.print(prompt + "[" + current + "]: ");
        String line = keyboard.nextLine().trim();

        return (line.isEmpty()) ? current : line;
    }

    /**
     * Asks until the user types a whole number.
     * <p>
     * {@link NumberFormatException} is unchecked, but it is caught here
     * deliberately: a mistyped number is an expected event and this is the one
     * place that can recover from it, by asking again.
     *
     * @param prompt the question to show
     * @return the number typed
     */
    public int readInt(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = keyboard.nextLine().trim();

            try
            {
                return Integer.parseInt(line);
            }
            catch (NumberFormatException ex) // "catch where you can recover"
            {
                System.out.println("Παρακαλώ εισάγετε έναν ακέραιο αριθμό");
            }
        }
    }

    /**
     * Like {@link #readInt(String)}, but an empty line keeps the current value.
     *
     * @param prompt  the question to show
     * @param current the existing value, returned if the user types nothing
     * @return the number typed, or {@code current}
     */
    public int readIntOrKeep(String prompt, int current)
    {
        while (true)
        {
            System.out.print(prompt + "[" + current + "]: ");
            String line = keyboard.nextLine().trim();

            try
            {
                return (line.isEmpty()) ? current : Integer.parseInt(line);
            }
            catch (NumberFormatException ex)
            {
                System.out.println("Παρακαλώ εισάγετε έναν ακέραιο αριθμό");
            }
        }
    }

    /**
     * Asks until the user types a number. A comma is accepted as the decimal
     * separator, so both {@code 8.5} and {@code 8,5} work.
     *
     * @param prompt the question to show
     * @return the number typed
     */
    public float readFloat(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = keyboard.nextLine().trim().replace(',', '.');

            try
            {
                return Float.parseFloat(line);
            }
            catch (NumberFormatException ex)
            {
                System.out.println("Παρακαλώ εισάγετε έναν δεκαδικό αριθμό");
            }
        }
    }

    /**
     * Like {@link #readFloat(String)}, but an empty line keeps the current value.
     *
     * @param prompt  the question to show
     * @param current the existing value, returned if the user types nothing
     * @return the number typed, or {@code current}
     */
    public float readFloatOrKeep(String prompt, float current)
    {
        while (true)
        {
            System.out.print(prompt + "[" + current + "]: ");
            String line = keyboard.nextLine().trim().replace(',', '.');

            try
            {
                return (line.isEmpty()) ? current : Float.parseFloat(line);
            }
            catch (NumberFormatException ex)
            {
                System.out.println("Παρακαλώ εισάγετε έναν αριθμό.\n");
            }
        }
    }

    /**
     * Asks a yes/no question, for destructive actions.
     * <p>
     * Only a clear yes ({@code ν}, {@code ναι}, {@code y}, {@code yes}, in any
     * capitalization) returns {@code true}. Anything else, including a blank
     * line or a typo, means no, so a stray keystroke cancels instead of
     * deleting. The "(ν/ο)" suffix is added here so that menus cannot mistype
     * it, for example with a Latin {@code n}, which looks like the Greek
     * {@code ν} but is a different character.
     *
     * @param question the question, without the yes/no suffix
     * @return {@code true} only if the user clearly answered yes
     */
    public boolean confirm(String question)
    {
        System.out.print(question + " (ν/ο): ");
        String line = keyboard.nextLine().trim();

        return line.equalsIgnoreCase("ν") || line.equalsIgnoreCase("ναι")
                || line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes");
    }

    /** Closes the underlying {@code Scanner}, and with it {@code System.in}. */
    public void close()
    {
        keyboard.close();
    }
}
