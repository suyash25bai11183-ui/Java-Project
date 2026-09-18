package clubgear;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Small helper class that reads input from the keyboard safely.
 *
 * Every method keeps asking until the user types something valid,
 * so a wrong entry never crashes the program (business rule 12).
 *
 * All text input is passed through cleanText(), which removes commas.
 * This is done because the data files use commas as separators,
 * and a comma inside a name would break the CSV line.
 */
public class InputValidator {

    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads one raw line from the keyboard.
     * If the input stream ends (for example when the program is run with a
     * redirected input file), the program stops politely instead of looping forever.
     */
    private String readLine() {
        try {
            if (!scanner.hasNextLine()) {
                System.out.println("\nInput has ended. Closing ClubGear.");
                System.exit(0);
            }
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("\nInput has ended. Closing ClubGear.");
            System.exit(0);
            return "";
        }
    }

    /** Removes commas and extra spaces so the text is safe to store in a CSV file. */
    public static String cleanText(String text) {
        return text.replace(",", " ").replace("\n", " ").trim();
    }

    /** Asks for a whole number between min and max (both included). */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = readLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid number. Please try again.");
            }
        }
    }

    /** Asks for some text that cannot be left empty. */
    public String readNonEmptyText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = cleanText(readLine());
            if (text.isEmpty()) {
                System.out.println("This field cannot be empty. Please try again.");
                continue;
            }
            return text;
        }
    }

    /** Asks for an ID and returns it in capital letters, for example "clb001" -> "CLB001". */
    public String readId(String prompt) {
        return readNonEmptyText(prompt).toUpperCase();
    }

    /** Simple email check: must contain one @ and a dot after it. */
    public String readEmail(String prompt) {
        while (true) {
            String email = readNonEmptyText(prompt).toLowerCase();
            if (isValidEmail(email)) {
                return email;
            }
            System.out.println("Invalid email format. Example: student@college.edu");
        }
    }

    public static boolean isValidEmail(String email) {
        int at = email.indexOf('@');
        int lastAt = email.lastIndexOf('@');
        if (at <= 0 || at != lastAt) {
            return false;
        }
        String domain = email.substring(at + 1);
        return domain.contains(".") && !domain.startsWith(".") && !domain.endsWith(".");
    }

    /** Asks for a date written as YYYY-MM-DD. */
    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = readLine().trim();
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please use the format YYYY-MM-DD (example: "
                        + LocalDate.now() + ").");
            }
        }
    }

    /** Asks a yes / no question. */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String answer = readLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            System.out.println("Please type y or n.");
        }
    }

    /** Waits until the user presses Enter, so the screen output can be read. */
    public void pressEnterToContinue() {
        System.out.print("\nPress Enter to go back to the menu...");
        readLine();
    }
}
