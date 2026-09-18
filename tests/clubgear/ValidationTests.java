package clubgear;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * ValidationTests: a small self-written test suite for ClubGear.
 *
 * No external testing library (such as JUnit) is used, because the project is
 * restricted to core Java. Instead, this class contains a tiny test harness:
 * every check() call compares an actual value with an expected value and prints
 * PASS or FAIL. At the end a summary is printed and the exit code is 0 when all
 * tests pass and 1 when at least one test fails.
 *
 * These are "validation tests": they check the rules that do not need keyboard
 * input, which means the date overlap logic, the status logic, the input
 * validation helpers and the CSV save/load conversion.
 *
 * How to compile and run (from the project root folder):
 *   javac -d out src/clubgear/*.java
 *   javac -d out-tests -cp out tests/clubgear/ValidationTests.java
 *   java -cp out:out-tests clubgear.ValidationTests        (Linux / macOS)
 *   java -cp out;out-tests clubgear.ValidationTests        (Windows)
 */
public class ValidationTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("        ClubGear - Automated Validation Tests");
        System.out.println("=========================================================\n");

        testDateOverlapLogic();
        testBookingStatusLogic();
        testEquipmentStatusLogic();
        testQuantityRule();
        testEmailValidation();
        testTextCleaning();
        testCsvRoundTrips();
        testBrokenCsvLines();
        testFileStorage();

        System.out.println("\n---------------------------------------------------------");
        System.out.println("Total tests : " + (passed + failed));
        System.out.println("Passed      : " + passed);
        System.out.println("Failed      : " + failed);
        System.out.println("---------------------------------------------------------");

        if (failed == 0) {
            System.out.println("RESULT: ALL TESTS PASSED");
        } else {
            System.out.println("RESULT: SOME TESTS FAILED");
        }
        System.exit(failed == 0 ? 0 : 1);
    }

    // =====================================================================
    // Tiny test harness
    // =====================================================================

    private static void check(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + testName);
        } else {
            failed++;
            System.out.println("  FAIL  " + testName);
        }
    }

    private static void section(String title) {
        System.out.println("\n[" + title + "]");
    }

    /** Builds a booking quickly for testing purposes. */
    private static Booking makeBooking(String id, LocalDate start, LocalDate end, BookingStatus status) {
        return new Booking(id, "MEM001", "EQP001", "CLB001", start, end,
                "Testing purpose", status, LocalDateTime.of(2026, 1, 1, 10, 0));
    }

    // =====================================================================
    // 1. Date overlap rules (business rules 5 and 6)
    // =====================================================================

    private static void testDateOverlapLogic() {
        section("Booking date overlap logic");

        LocalDate day10 = LocalDate.of(2026, 3, 10);
        LocalDate day14 = LocalDate.of(2026, 3, 14);
        Booking existing = makeBooking("BKG001", day10, day14, BookingStatus.APPROVED);

        check("Fully inside the existing range overlaps",
                existing.overlapsWith(LocalDate.of(2026, 3, 11), LocalDate.of(2026, 3, 13)));

        check("Starts before and ends inside overlaps",
                existing.overlapsWith(LocalDate.of(2026, 3, 8), LocalDate.of(2026, 3, 11)));

        check("Starts inside and ends after overlaps",
                existing.overlapsWith(LocalDate.of(2026, 3, 13), LocalDate.of(2026, 3, 20)));

        check("Completely surrounding range overlaps",
                existing.overlapsWith(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31)));

        check("Same start and end date overlaps",
                existing.overlapsWith(day10, day14));

        check("Range touching the first day overlaps",
                existing.overlapsWith(LocalDate.of(2026, 3, 5), day10));

        check("Range touching the last day overlaps",
                existing.overlapsWith(day14, LocalDate.of(2026, 3, 18)));

        check("Range finishing one day earlier does NOT overlap",
                !existing.overlapsWith(LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 9)));

        check("Range starting one day later does NOT overlap",
                !existing.overlapsWith(LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 18)));

        check("Range in a different month does NOT overlap",
                !existing.overlapsWith(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5)));
    }

    // =====================================================================
    // 2. Which booking states block the equipment
    // =====================================================================

    private static void testBookingStatusLogic() {
        section("Booking status logic");

        LocalDate start = LocalDate.of(2026, 5, 1);
        LocalDate end = LocalDate.of(2026, 5, 3);

        check("REQUESTED booking blocks the equipment",
                makeBooking("B1", start, end, BookingStatus.REQUESTED).isBlockingEquipment());
        check("APPROVED booking blocks the equipment",
                makeBooking("B2", start, end, BookingStatus.APPROVED).isBlockingEquipment());
        check("ACTIVE booking blocks the equipment",
                makeBooking("B3", start, end, BookingStatus.ACTIVE).isBlockingEquipment());
        check("OVERDUE booking blocks the equipment",
                makeBooking("B4", start, end, BookingStatus.OVERDUE).isBlockingEquipment());
        check("RETURNED booking does NOT block the equipment",
                !makeBooking("B5", start, end, BookingStatus.RETURNED).isBlockingEquipment());
        check("CANCELLED booking does NOT block the equipment",
                !makeBooking("B6", start, end, BookingStatus.CANCELLED).isBlockingEquipment());

        check("BookingStatus enum has exactly six states",
                BookingStatus.values().length == 6);
    }

    // =====================================================================
    // 3. Which equipment states can be booked (business rule 4)
    // =====================================================================

    private static void testEquipmentStatusLogic() {
        section("Equipment status logic");

        Equipment available = new Equipment("EQP001", "Camera", "Camera", 1, "Good",
                "CLB001", EquipmentStatus.AVAILABLE);
        Equipment booked = new Equipment("EQP002", "Mic", "Audio", 1, "Good",
                "CLB001", EquipmentStatus.BOOKED);
        Equipment damaged = new Equipment("EQP003", "Light", "Lighting", 1, "Poor",
                "CLB001", EquipmentStatus.DAMAGED);
        Equipment maintenance = new Equipment("EQP004", "Projector", "Display", 1, "Average",
                "CLB001", EquipmentStatus.UNDER_MAINTENANCE);

        check("AVAILABLE equipment can be booked", available.isBookable());
        check("BOOKED equipment can still be booked for other dates", booked.isBookable());
        check("DAMAGED equipment cannot be booked", !damaged.isBookable());
        check("UNDER_MAINTENANCE equipment cannot be booked", !maintenance.isBookable());

        check("EquipmentStatus enum has exactly four states",
                EquipmentStatus.values().length == 4);
    }

    // =====================================================================
    // 4. Quantity rule (business rule 3)
    // =====================================================================

    private static void testQuantityRule() {
        section("Equipment quantity rule");

        Equipment equipment = new Equipment("EQP001", "Tripod", "Camera", 3, "Good",
                "CLB001", EquipmentStatus.AVAILABLE);

        equipment.setQuantity(7);
        check("A positive quantity is accepted", equipment.getQuantity() == 7);

        equipment.setQuantity(0);
        check("Quantity zero is rejected and the old value stays", equipment.getQuantity() == 7);

        equipment.setQuantity(-5);
        check("A negative quantity is rejected", equipment.getQuantity() == 7);
    }

    // =====================================================================
    // 5. Email validation (supports business rule 2)
    // =====================================================================

    private static void testEmailValidation() {
        section("Email validation");

        check("Normal college email is accepted",
                InputValidator.isValidEmail("aditya.verma@college.edu"));
        check("Email with numbers is accepted",
                InputValidator.isValidEmail("student23bce1234@vitstudent.ac.in"));
        check("Email without @ is rejected",
                !InputValidator.isValidEmail("adityacollege.edu"));
        check("Email with two @ symbols is rejected",
                !InputValidator.isValidEmail("aditya@@college.edu"));
        check("Email without a dot in the domain is rejected",
                !InputValidator.isValidEmail("aditya@college"));
        check("Email starting with @ is rejected",
                !InputValidator.isValidEmail("@college.edu"));
        check("Email ending with a dot is rejected",
                !InputValidator.isValidEmail("aditya@college."));
    }

    // =====================================================================
    // 6. Text cleaning keeps the CSV format safe
    // =====================================================================

    private static void testTextCleaning() {
        section("Text cleaning for CSV safety");

        String cleaned = InputValidator.cleanText("Camera, tripod, and lens");
        check("Commas are removed from typed text", !cleaned.contains(","));
        check("The words survive the cleaning", cleaned.contains("Camera") && cleaned.contains("lens"));
        check("Leading and trailing spaces are removed",
                InputValidator.cleanText("   Coding Club   ").equals("Coding Club"));
    }

    // =====================================================================
    // 7. CSV conversion must work in both directions
    // =====================================================================

    private static void testCsvRoundTrips() {
        section("CSV save and load round trips");

        Club club = new Club("CLB001", "Photography Club", "Prof. Anita Sharma", "Cultural");
        Club clubBack = Club.fromCsvLine(club.toCsvLine());
        check("Club survives the CSV round trip",
                clubBack != null
                        && clubBack.getClubId().equals("CLB001")
                        && clubBack.getName().equals("Photography Club")
                        && clubBack.getCoordinatorName().equals("Prof. Anita Sharma")
                        && clubBack.getCategory().equals("Cultural"));

        Member member = new Member("MEM002", "Sneha Patil", "sneha@college.edu", "IT", 3, "CLB002");
        Member memberBack = Member.fromCsvLine(member.toCsvLine());
        check("Member survives the CSV round trip",
                memberBack != null
                        && memberBack.getMemberId().equals("MEM002")
                        && memberBack.getEmail().equals("sneha@college.edu")
                        && memberBack.getYear() == 3
                        && memberBack.getClubId().equals("CLB002"));

        Equipment equipment = new Equipment("EQP005", "LED Light Panel", "Lighting", 2, "Poor",
                "CLB001", EquipmentStatus.DAMAGED);
        Equipment equipmentBack = Equipment.fromCsvLine(equipment.toCsvLine());
        check("Equipment survives the CSV round trip",
                equipmentBack != null
                        && equipmentBack.getQuantity() == 2
                        && equipmentBack.getStatus() == EquipmentStatus.DAMAGED
                        && equipmentBack.getName().equals("LED Light Panel"));

        Booking fresh = makeBooking("BKG007", LocalDate.of(2026, 4, 2),
                LocalDate.of(2026, 4, 5), BookingStatus.APPROVED);
        Booking freshBack = Booking.fromCsvLine(fresh.toCsvLine());
        check("New booking survives the CSV round trip",
                freshBack != null
                        && freshBack.getBookingId().equals("BKG007")
                        && freshBack.getStartDate().equals(LocalDate.of(2026, 4, 2))
                        && freshBack.getExpectedReturnDate().equals(LocalDate.of(2026, 4, 5))
                        && freshBack.getStatus() == BookingStatus.APPROVED);
        check("Empty return date is stored as NONE and read back as null",
                freshBack != null && freshBack.getActualReturnDate() == null
                        && freshBack.getReturnCondition() == null
                        && freshBack.getDamageNotes() == null);

        Booking returned = makeBooking("BKG008", LocalDate.of(2026, 4, 2),
                LocalDate.of(2026, 4, 5), BookingStatus.RETURNED);
        returned.setActualReturnDate(LocalDate.of(2026, 4, 6));
        returned.setReturnCondition("Damaged");
        returned.setDamageNotes("Battery cover broken");
        Booking returnedBack = Booking.fromCsvLine(returned.toCsvLine());
        check("Returned booking keeps its return details after the round trip",
                returnedBack != null
                        && returnedBack.getActualReturnDate().equals(LocalDate.of(2026, 4, 6))
                        && returnedBack.getReturnCondition().equals("Damaged")
                        && returnedBack.getDamageNotes().equals("Battery cover broken"));

        MaintenanceRecord record = new MaintenanceRecord("MNT001", "EQP005",
                LocalDate.of(2026, 2, 9), "DAMAGE_REPORTED", "Panel flickers");
        MaintenanceRecord recordBack = MaintenanceRecord.fromCsvLine(record.toCsvLine());
        check("Maintenance record survives the CSV round trip",
                recordBack != null
                        && recordBack.getEquipmentId().equals("EQP005")
                        && recordBack.getAction().equals("DAMAGE_REPORTED")
                        && recordBack.getRecordDate().equals(LocalDate.of(2026, 2, 9)));
    }

    // =====================================================================
    // 8. A damaged file line must be skipped, not crash the program
    // =====================================================================

    private static void testBrokenCsvLines() {
        section("Damaged CSV lines are skipped safely");

        check("Club line with too few columns returns null",
                Club.fromCsvLine("CLB001,Photography Club") == null);
        check("Member line with a non-numeric year returns null",
                Member.fromCsvLine("MEM001,Aditya,a@b.com,CSE,second,CLB001") == null);
        check("Equipment line with an unknown status returns null",
                Equipment.fromCsvLine("EQP001,Camera,Camera,2,Good,CLB001,FLYING") == null);
        check("Equipment line with a non-numeric quantity returns null",
                Equipment.fromCsvLine("EQP001,Camera,Camera,two,Good,CLB001,AVAILABLE") == null);
        check("Booking line with a bad date returns null",
                Booking.fromCsvLine("BKG001,MEM001,EQP001,CLB001,10-03-2026,2026-03-14,NONE,"
                        + "Event,APPROVED,2026-01-01T10:00,NONE,NONE") == null);
        check("Empty line returns null instead of throwing an exception",
                MaintenanceRecord.fromCsvLine("") == null);
    }

    // =====================================================================
    // 9. File storage: missing files and a real write / read cycle
    // =====================================================================

    private static void testFileStorage() {
        section("File storage");

        FileStorage storage = new FileStorage();

        ArrayList<String> missing = storage.readLines("this_file_does_not_exist.csv");
        check("Reading a missing file returns an empty list instead of crashing",
                missing != null && missing.isEmpty());

        ArrayList<String> lines = new ArrayList<>();
        lines.add("CLB001,Photography Club,Prof. Anita Sharma,Cultural");
        lines.add("CLB002,Coding Club,Prof. Ravi Menon,Technical");

        boolean written = storage.writeLines("validation_test_temp.csv",
                "clubId,name,coordinatorName,category", lines);
        check("Writing a file reports success", written);

        ArrayList<String> readBack = storage.readLines("validation_test_temp.csv");
        check("The same number of data lines is read back", readBack.size() == 2);
        check("The header comment line is not returned as data",
                readBack.size() == 2 && !readBack.get(0).startsWith("#"));
        check("The content of the first line is unchanged",
                readBack.size() == 2
                        && readBack.get(0).equals("CLB001,Photography Club,Prof. Anita Sharma,Cultural"));

        Club parsed = readBack.isEmpty() ? null : Club.fromCsvLine(readBack.get(0));
        check("A club read from the file can be rebuilt as an object",
                parsed != null && parsed.getName().equals("Photography Club"));

        // Clean up the temporary file so the data folder is left tidy.
        File temp = new File(FileStorage.DATA_FOLDER, "validation_test_temp.csv");
        boolean deleted = temp.delete();
        check("The temporary test file is deleted after the test", deleted);
    }
}
