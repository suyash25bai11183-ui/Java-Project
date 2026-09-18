package clubgear;

import java.util.Scanner;

/**
 * ClubGear: A Campus Club Equipment Booking and Management System
 *
 * This is the starting point of the program.
 * It only prints a welcome banner and hands over the work to ClubGearSystem.
 *
 * How to run (from the project folder):
 *   javac -d out src/clubgear/*.java
 *   java -cp out clubgear.Main
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("**************************************************");
        System.out.println("*                   CLUBGEAR                     *");
        System.out.println("*  Campus Club Equipment Booking and Management   *");
        System.out.println("**************************************************");

        // One Scanner object is created here and shared by the whole program.
        Scanner scanner = new Scanner(System.in);

        ClubGearSystem system = new ClubGearSystem(scanner);
        system.startUp();
        system.start();

        scanner.close();
    }
}
