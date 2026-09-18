package clubgear;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading data using plain CSV text files.
 * No external library is used, only the classes from java.io.
 *
 * All files are kept inside a folder called "data", which is created
 * automatically the first time the user saves:
 *
 *   data/clubs.csv        -> clubId,name,coordinatorName,category
 *   data/members.csv      -> memberId,name,email,course,year,clubId
 *   data/equipment.csv    -> equipmentId,name,category,quantity,condition,ownerClubId,status
 *   data/bookings.csv     -> bookingId,memberId,equipmentId,clubId,startDate,expectedReturnDate,
 *                            actualReturnDate,purpose,status,createdAt,returnCondition,damageNotes
 *   data/maintenance.csv  -> recordId,equipmentId,recordDate,action,notes
 *
 * Each file starts with one header line beginning with '#'.
 * Lines starting with '#' are ignored while reading.
 *
 * Commas are not allowed inside the values. InputValidator.cleanText()
 * removes them from every text the user types, so the format stays safe.
 */
public class FileStorage {

    public static final String DATA_FOLDER = "data";

    public static final String CLUBS_FILE = "clubs.csv";
    public static final String MEMBERS_FILE = "members.csv";
    public static final String EQUIPMENT_FILE = "equipment.csv";
    public static final String BOOKINGS_FILE = "bookings.csv";
    public static final String MAINTENANCE_FILE = "maintenance.csv";

    /** Makes sure the data folder exists before writing into it. */
    private void createFolderIfMissing() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Writes all the given lines into one file inside the data folder.
     * Returns true if the file was written successfully.
     */
    public boolean writeLines(String fileName, String header, List<String> lines) {
        createFolderIfMissing();
        File file = new File(DATA_FOLDER, fileName);

        // try-with-resources closes the writer automatically, even if an error happens.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("# " + header);
            writer.newLine();
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Could not save file " + fileName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads all useful lines from one file inside the data folder.
     * If the file does not exist, an empty list is returned
     * so that a missing file never crashes the program.
     */
    public ArrayList<String> readLines(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File(DATA_FOLDER, fileName);

        if (!file.exists()) {
            return lines;   // nothing saved yet, this is not an error
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String trimmed = line.trim();
                // Skip empty lines and comment/header lines.
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    lines.add(trimmed);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Could not read file " + fileName + ": " + e.getMessage());
        }
        return lines;
    }

    /** Tells whether at least one saved data file is present on the disk. */
    public boolean savedDataExists() {
        String[] files = {CLUBS_FILE, MEMBERS_FILE, EQUIPMENT_FILE, BOOKINGS_FILE, MAINTENANCE_FILE};
        for (String name : files) {
            if (new File(DATA_FOLDER, name).exists()) {
                return true;
            }
        }
        return false;
    }
}
