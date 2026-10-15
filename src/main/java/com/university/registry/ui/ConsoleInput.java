package com.university.registry.ui;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsoleInput
{
    Scanner keyboard;

    public ConsoleInput()
    {
        keyboard = new Scanner(System.in, StandardCharsets.UTF_8);
    }

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

    public String readStringOrKeep(String prompt, String current)
    {
        System.out.print(prompt + "[" + current + "]: ");
        String line = keyboard.nextLine().trim();

        return (line.isEmpty()) ? current : line;
    }

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
            catch (NumberFormatException ex)
            {
                System.out.println("Παρακαλώ εισάγεται έναν ακέραιο αριθμό");
            }
        }
    }

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
                System.out.println("Παρακαλώ εισάγεται έναν ακέραιο αριθμό");
            }
        }
    }

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
                System.out.println("Παρακαλώ εισάγεται έναν δεκαδικό αριθμό");
            }
        }
    }

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
                System.out.println("Παρακαλώ εισάγεται έναν δεκαδικό αριθμό");
            }
        }
    }

    public boolean confirm(String prompt)
    {
        System.out.print(prompt);
        String line = keyboard.nextLine().trim();

        return line.equalsIgnoreCase("ν") || line.equalsIgnoreCase("ναι")
                || line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes");
    }

    public void close()
    {
        keyboard.close();
    }
}
