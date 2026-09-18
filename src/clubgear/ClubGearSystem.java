package clubgear;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

/**
 * The heart of the ClubGear application.
 *
 * This class keeps all the data in ArrayLists, shows the menu,
 * and contains one short method for every operation of the menu.
 */
public class ClubGearSystem {

    // ---------- Data stored in memory ----------
    private final ArrayList<Club> clubs = new ArrayList<>();
    private final ArrayList<Member> members = new ArrayList<>();
    private final ArrayList<Equipment> equipmentList = new ArrayList<>();
    private final ArrayList<Booking> bookings = new ArrayList<>();
    private final ArrayList<MaintenanceRecord> maintenanceRecords = new ArrayList<>();

    // A HashSet gives a very fast duplicate check for emails (business rule 2).
    private final HashSet<String> registeredEmails = new HashSet<>();

    // ---------- Counters used to build the next unique ID ----------
    private int clubCounter = 1;
    private int memberCounter = 1;
    private int equipmentCounter = 1;
    private int bookingCounter = 1;
    private int maintenanceCounter = 1;

    private final InputValidator input;
    private final FileStorage storage = new FileStorage();

    public ClubGearSystem(Scanner scanner) {
        this.input = new InputValidator(scanner);
    }

    // =====================================================================
    // Startup
    // =====================================================================

    /** Loads saved data if it exists, otherwise fills the system with sample data. */
    public void startUp() {
        if (storage.savedDataExists()) {
            System.out.println("Saved data found. Loading it now...");
            loadData();
        } else {
            System.out.println("No saved data found in the 'data' folder.");
            System.out.println("Loading built-in sample data for demonstration.");
            loadSampleData();
        }
    }

    /** Shows the menu again and again until the user chooses Exit. */
    public void start() {
        boolean running = true;
        while (running) {
            updateOverdueBookings();
            showMenu();
            int choice = input.readInt("Enter your choice (1-16): ", 1, 16);
            switch (choice) {
                case 1 -> addClub();
                case 2 -> clubViewMenu();
                case 3 -> memberMenu();
                case 4 -> addEquipment();
                case 5 -> equipmentViewMenu();
                case 6 -> createBooking();
                case 7 -> viewBookings();
                case 8 -> approveBooking();
                case 9 -> cancelBooking();
                case 10 -> issueEquipment();
                case 11 -> returnEquipment();
                case 12 -> manageMaintenance();
                case 13 -> viewReports();
                case 14 -> saveData();
                case 15 -> loadData();
                case 16 -> {
                    exitProgram();
                    running = false;
                }
            }
        }
    }

    private void showMenu() {
        System.out.println("\n==================================================");
        System.out.println("            CLUBGEAR  -  MAIN MENU");
        System.out.println("   Today's date: " + LocalDate.now());
        System.out.println("==================================================");
        System.out.println(" 1. Add Club");
        System.out.println(" 2. View / Search Clubs");
        System.out.println(" 3. Register / View Members");
        System.out.println(" 4. Add Equipment");
        System.out.println(" 5. View / Search / Update Equipment");
        System.out.println(" 6. Create Equipment Booking");
        System.out.println(" 7. View Bookings");
        System.out.println(" 8. Approve Booking");
        System.out.println(" 9. Cancel Booking");
        System.out.println("10. Issue Equipment");
        System.out.println("11. Return Equipment");
        System.out.println("12. Manage Maintenance");
        System.out.println("13. View Reports");
        System.out.println("14. Save Data");
        System.out.println("15. Load Data");
        System.out.println("16. Exit");
        System.out.println("--------------------------------------------------");
    }

    private void exitProgram() {
        if (input.readYesNo("Do you want to save your data before exiting?")) {
            saveData();
        }
        System.out.println("\nThank you for using ClubGear. Goodbye!");
    }

    // =====================================================================
    // 1 & 2. Club management
    // =====================================================================

    private void addClub() {
        System.out.println("\n----- Add New Club -----");
        String name = input.readNonEmptyText("Club name              : ");

        // Business rule 1: club names must be unique.
        if (findClubByName(name) != null) {
            System.out.println("Error: a club named '" + name + "' already exists.");
            return;
        }

        String coordinator = input.readNonEmptyText("Coordinator name       : ");
        String category = input.readNonEmptyText("Category (Technical/Cultural/Sports): ");

        Club club = new Club(nextId("CLB", clubCounter), name, coordinator, category);
        clubCounter++;
        clubs.add(club);
        System.out.println("Club added successfully. Club ID: " + club.getClubId());
    }

    private void clubViewMenu() {
        System.out.println("\n----- Clubs -----");
        System.out.println("1. View all clubs");
        System.out.println("2. Search club by name");
        System.out.println("3. Search club by ID");
        System.out.println("4. Back to main menu");
        int choice = input.readInt("Choice: ", 1, 4);

        switch (choice) {
            case 1 -> printClubs(clubs);
            case 2 -> searchClubByName();
            case 3 -> searchClubById();
            case 4 -> { /* nothing to do, simply go back */ }
        }
    }

    private void searchClubByName() {
        String text = input.readNonEmptyText("Enter full or part of the club name: ").toLowerCase();
        ArrayList<Club> found = new ArrayList<>();
        for (Club club : clubs) {
            if (club.getName().toLowerCase().contains(text)) {
                found.add(club);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No club matched your search.");
        } else {
            printClubs(found);
        }
    }

    private void searchClubById() {
        String id = input.readId("Enter club ID (example CLB001): ");
        Club club = findClubById(id);
        if (club == null) {
            System.out.println("No club found with ID " + id + ".");
            return;
        }
        ArrayList<Club> one = new ArrayList<>();
        one.add(club);
        printClubs(one);
    }

    private void printClubs(ArrayList<Club> list) {
        if (list.isEmpty()) {
            System.out.println("There are no clubs to show.");
            return;
        }
        System.out.println("\n" + String.format("%-8s %-25s %-22s %-15s",
                "ID", "CLUB NAME", "COORDINATOR", "CATEGORY"));
        System.out.println("---------------------------------------------------------------------------");
        for (Club club : list) {
            System.out.println(club);
        }
        System.out.println("Total clubs shown: " + list.size());
    }

    // =====================================================================
    // 3. Member management
    // =====================================================================

    private void memberMenu() {
        System.out.println("\n----- Members -----");
        System.out.println("1. Register a new member");
        System.out.println("2. View all members");
        System.out.println("3. Search member by name");
        System.out.println("4. Search member by ID");
        System.out.println("5. Back to main menu");
        int choice = input.readInt("Choice: ", 1, 5);

        switch (choice) {
            case 1 -> registerMember();
            case 2 -> printMembers(members);
            case 3 -> searchMemberByName();
            case 4 -> searchMemberById();
            case 5 -> { /* go back */ }
        }
    }

    private void registerMember() {
        System.out.println("\n----- Register New Member -----");
        if (clubs.isEmpty()) {
            System.out.println("Please add at least one club before registering members.");
            return;
        }

        String name = input.readNonEmptyText("Student name : ");
        String email = input.readEmail("Email        : ");

        // Business rule 2: one email can be registered only once.
        if (registeredEmails.contains(email)) {
            System.out.println("Error: this email is already registered.");
            return;
        }

        String course = input.readNonEmptyText("Course (CSE/IT/ECE/etc.) : ");
        int year = input.readInt("Year of study (1-4)      : ", 1, 4);

        printClubs(clubs);
        String clubId = input.readId("Enter the club ID to join: ");
        if (findClubById(clubId) == null) {
            System.out.println("Error: no club exists with ID " + clubId + ".");
            return;
        }

        Member member = new Member(nextId("MEM", memberCounter), name, email, course, year, clubId);
        memberCounter++;
        members.add(member);
        registeredEmails.add(email);
        System.out.println("Member registered successfully. Member ID: " + member.getMemberId());
    }

    private void searchMemberByName() {
        String text = input.readNonEmptyText("Enter full or part of the member name: ").toLowerCase();
        ArrayList<Member> found = new ArrayList<>();
        for (Member member : members) {
            if (member.getName().toLowerCase().contains(text)) {
                found.add(member);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No member matched your search.");
        } else {
            printMembers(found);
        }
    }

    private void searchMemberById() {
        String id = input.readId("Enter member ID (example MEM001): ");
        Member member = findMemberById(id);
        if (member == null) {
            System.out.println("No member found with ID " + id + ".");
            return;
        }
        ArrayList<Member> one = new ArrayList<>();
        one.add(member);
        printMembers(one);
    }

    private void printMembers(ArrayList<Member> list) {
        if (list.isEmpty()) {
            System.out.println("There are no members to show.");
            return;
        }
        System.out.println("\n" + String.format("%-8s %-20s %-28s %-8s %-5s %-8s",
                "ID", "NAME", "EMAIL", "COURSE", "YEAR", "CLUB"));
        System.out.println("---------------------------------------------------------------------------------");
        for (Member member : list) {
            System.out.println(member);
        }
        System.out.println("Total members shown: " + list.size());
    }

    // =====================================================================
    // 4 & 5. Equipment management
    // =====================================================================

    private void addEquipment() {
        System.out.println("\n----- Add New Equipment -----");
        if (clubs.isEmpty()) {
            System.out.println("Please add at least one club before adding equipment.");
            return;
        }

        String name = input.readNonEmptyText("Equipment name  : ");
        String category = input.readNonEmptyText("Category (Camera/Audio/Display/Lighting/Other): ");

        // Business rule 3: quantity must be greater than zero.
        int quantity = input.readInt("Quantity (1-500): ", 1, 500);
        String condition = input.readNonEmptyText("Condition (Good/Average/Poor): ");

        printClubs(clubs);
        String clubId = input.readId("Owning club ID  : ");
        if (findClubById(clubId) == null) {
            System.out.println("Error: no club exists with ID " + clubId + ".");
            return;
        }

        Equipment equipment = new Equipment(nextId("EQP", equipmentCounter), name, category,
                quantity, condition, clubId, EquipmentStatus.AVAILABLE);
        equipmentCounter++;
        equipmentList.add(equipment);
        System.out.println("Equipment added successfully. Equipment ID: " + equipment.getEquipmentId());
    }

    private void equipmentViewMenu() {
        System.out.println("\n----- Equipment -----");
        System.out.println("1. View all equipment");
        System.out.println("2. Search equipment by name");
        System.out.println("3. Search equipment by category");
        System.out.println("4. Update equipment condition");
        System.out.println("5. Back to main menu");
        int choice = input.readInt("Choice: ", 1, 5);

        switch (choice) {
            case 1 -> printEquipment(equipmentList);
            case 2 -> searchEquipmentByName();
            case 3 -> searchEquipmentByCategory();
            case 4 -> updateEquipmentCondition();
            case 5 -> { /* go back */ }
        }
    }

    private void searchEquipmentByName() {
        String text = input.readNonEmptyText("Enter full or part of the equipment name: ").toLowerCase();
        ArrayList<Equipment> found = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            if (equipment.getName().toLowerCase().contains(text)) {
                found.add(equipment);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No equipment matched your search.");
        } else {
            printEquipment(found);
        }
    }

    private void searchEquipmentByCategory() {
        String text = input.readNonEmptyText("Enter category: ").toLowerCase();
        ArrayList<Equipment> found = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            if (equipment.getCategory().toLowerCase().contains(text)) {
                found.add(equipment);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No equipment found in that category.");
        } else {
            printEquipment(found);
        }
    }

    private void updateEquipmentCondition() {
        printEquipment(equipmentList);
        if (equipmentList.isEmpty()) {
            return;
        }
        String id = input.readId("Enter equipment ID to update: ");
        Equipment equipment = findEquipmentById(id);
        if (equipment == null) {
            System.out.println("No equipment found with ID " + id + ".");
            return;
        }
        String condition = input.readNonEmptyText("New condition (Good/Average/Poor): ");
        equipment.setCondition(condition);
        System.out.println("Condition of " + equipment.getName() + " updated to " + condition + ".");
    }

    private void printEquipment(ArrayList<Equipment> list) {
        if (list.isEmpty()) {
            System.out.println("There is no equipment to show.");
            return;
        }
        System.out.println("\n" + String.format("%-8s %-24s %-14s %-4s %-9s %-8s %-18s",
                "ID", "NAME", "CATEGORY", "QTY", "CONDITION", "CLUB", "STATUS"));
        System.out.println("---------------------------------------------------------------------------------------");
        for (Equipment equipment : list) {
            System.out.println(equipment);
        }
        System.out.println("Total equipment shown: " + list.size());
    }

    // =====================================================================
    // 6. Create a booking
    // =====================================================================

    private void createBooking() {
        System.out.println("\n----- Create Equipment Booking -----");
        if (members.isEmpty() || equipmentList.isEmpty()) {
            System.out.println("Members and equipment must exist before a booking can be made.");
            return;
        }

        printMembers(members);
        String memberId = input.readId("Enter member ID: ");
        Member member = findMemberById(memberId);
        if (member == null) {
            System.out.println("Error: no member found with ID " + memberId + ".");
            return;
        }

        printEquipment(equipmentList);
        String equipmentId = input.readId("Enter equipment ID: ");
        Equipment equipment = findEquipmentById(equipmentId);
        if (equipment == null) {
            System.out.println("Error: no equipment found with ID " + equipmentId + ".");
            return;
        }

        // Business rule 4: damaged or under-maintenance equipment cannot be booked.
        if (!equipment.isBookable()) {
            System.out.println("Error: this equipment is " + equipment.getStatus()
                    + " and cannot be booked right now.");
            return;
        }

        String clubId = input.readId("Booking for which club ID (usually the owner "
                + equipment.getOwnerClubId() + "): ");
        if (findClubById(clubId) == null) {
            System.out.println("Error: no club exists with ID " + clubId + ".");
            return;
        }

        LocalDate startDate = input.readDate("Booking start date (YYYY-MM-DD) : ");
        if (startDate.isBefore(LocalDate.now())) {
            System.out.println("Error: the start date cannot be in the past.");
            return;
        }

        LocalDate returnDate = input.readDate("Expected return date (YYYY-MM-DD): ");
        // Business rule 7: the return date must not be before the start date.
        if (returnDate.isBefore(startDate)) {
            System.out.println("Error: the return date cannot be earlier than the start date.");
            return;
        }

        // Business rules 5 and 6: no two live bookings of the same item may overlap.
        Booking clash = findClashingBooking(equipmentId, startDate, returnDate);
        if (clash != null) {
            System.out.println("Error: this equipment is already booked from "
                    + clash.getStartDate() + " to " + clash.getExpectedReturnDate()
                    + " (booking " + clash.getBookingId() + ").");
            if (clash.getMemberId().equals(memberId)) {
                System.out.println("Note: that clashing booking belongs to the same member.");
            }
            return;
        }

        String purpose = input.readNonEmptyText("Purpose of booking: ");

        Booking booking = new Booking(nextId("BKG", bookingCounter), memberId, equipmentId, clubId,
                startDate, returnDate, purpose, BookingStatus.REQUESTED, LocalDateTime.now());
        bookingCounter++;
        bookings.add(booking);

        System.out.println("\n========== BOOKING CONFIRMATION ==========");
        System.out.println("Booking ID   : " + booking.getBookingId());
        System.out.println("Member       : " + member.getName() + " (" + memberId + ")");
        System.out.println("Equipment    : " + equipment.getName() + " (" + equipmentId + ")");
        System.out.println("Club         : " + clubNameOf(clubId));
        System.out.println("From         : " + startDate);
        System.out.println("Until        : " + returnDate);
        System.out.println("Purpose      : " + purpose);
        System.out.println("Status       : " + booking.getStatus());
        System.out.println("==========================================");
        System.out.println("The booking is waiting for coordinator approval (menu option 8).");
    }

    /** Returns the first live booking of the same equipment that clashes with these dates. */
    private Booking findClashingBooking(String equipmentId, LocalDate start, LocalDate end) {
        for (Booking booking : bookings) {
            if (booking.getEquipmentId().equals(equipmentId)
                    && booking.isBlockingEquipment()
                    && booking.overlapsWith(start, end)) {
                return booking;
            }
        }
        return null;
    }

    // =====================================================================
    // 7. View bookings
    // =====================================================================

    private void viewBookings() {
        System.out.println("\n----- Bookings -----");
        System.out.println("1. View all bookings");
        System.out.println("2. View only pending (requested) bookings");
        System.out.println("3. View only active bookings");
        System.out.println("4. View bookings of one member");
        System.out.println("5. Back to main menu");
        int choice = input.readInt("Choice: ", 1, 5);

        switch (choice) {
            case 1 -> printBookings(bookings);
            case 2 -> printBookings(filterByStatus(BookingStatus.REQUESTED));
            case 3 -> printBookings(filterByStatus(BookingStatus.ACTIVE));
            case 4 -> viewBookingsOfMember();
            case 5 -> { /* go back */ }
        }
    }

    private void viewBookingsOfMember() {
        String memberId = input.readId("Enter member ID: ");
        if (findMemberById(memberId) == null) {
            System.out.println("No member found with ID " + memberId + ".");
            return;
        }
        ArrayList<Booking> found = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getMemberId().equals(memberId)) {
                found.add(booking);
            }
        }
        printBookings(found);
    }

    private ArrayList<Booking> filterByStatus(BookingStatus status) {
        ArrayList<Booking> found = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStatus() == status) {
                found.add(booking);
            }
        }
        return found;
    }

    private void printBookings(ArrayList<Booking> list) {
        if (list.isEmpty()) {
            System.out.println("There are no bookings to show.");
            return;
        }
        System.out.println("\n" + String.format("%-8s %-8s %-8s %-8s %-11s %-11s %-11s %-10s",
                "BKG ID", "MEMBER", "EQUIP", "CLUB", "FROM", "DUE", "RETURNED", "STATUS"));
        System.out.println("-------------------------------------------------------------------------------------");
        for (Booking booking : list) {
            System.out.println(booking);
        }
        System.out.println("Total bookings shown: " + list.size());
    }

    // =====================================================================
    // 8, 9, 10, 11. Booking status changes
    // =====================================================================

    private void approveBooking() {
        System.out.println("\n----- Approve Booking -----");
        ArrayList<Booking> pending = filterByStatus(BookingStatus.REQUESTED);
        if (pending.isEmpty()) {
            System.out.println("There are no bookings waiting for approval.");
            return;
        }
        printBookings(pending);

        String id = input.readId("Enter booking ID to approve: ");
        Booking booking = findBookingById(id);
        if (booking == null) {
            System.out.println("No booking found with ID " + id + ".");
            return;
        }
        // Business rule 8: only a REQUESTED booking can be approved.
        if (booking.getStatus() != BookingStatus.REQUESTED) {
            System.out.println("Error: booking " + id + " is " + booking.getStatus()
                    + ", so it cannot be approved.");
            return;
        }
        booking.setStatus(BookingStatus.APPROVED);
        System.out.println("Booking " + id + " approved. It can now be issued (menu option 10).");
    }

    private void cancelBooking() {
        System.out.println("\n----- Cancel Booking -----");
        printBookings(bookings);
        if (bookings.isEmpty()) {
            return;
        }

        String id = input.readId("Enter booking ID to cancel: ");
        Booking booking = findBookingById(id);
        if (booking == null) {
            System.out.println("No booking found with ID " + id + ".");
            return;
        }

        BookingStatus status = booking.getStatus();
        if (status == BookingStatus.ACTIVE || status == BookingStatus.OVERDUE) {
            System.out.println("Error: the equipment is already issued. Please return it instead.");
            return;
        }
        if (status != BookingStatus.REQUESTED && status != BookingStatus.APPROVED) {
            System.out.println("Error: booking " + id + " is " + status + " and cannot be cancelled.");
            return;
        }

        booking.setStatus(BookingStatus.CANCELLED);
        // Business rule 10: a cancelled booking releases the equipment.
        releaseEquipment(booking.getEquipmentId());
        System.out.println("Booking " + id + " has been cancelled.");
    }

    private void issueEquipment() {
        System.out.println("\n----- Issue Equipment -----");
        ArrayList<Booking> approved = filterByStatus(BookingStatus.APPROVED);
        if (approved.isEmpty()) {
            System.out.println("There are no approved bookings ready to be issued.");
            return;
        }
        printBookings(approved);

        String id = input.readId("Enter booking ID to issue: ");
        Booking booking = findBookingById(id);
        if (booking == null) {
            System.out.println("No booking found with ID " + id + ".");
            return;
        }
        // Business rule 8: only an approved booking can become active.
        if (booking.getStatus() != BookingStatus.APPROVED) {
            System.out.println("Error: booking " + id + " is " + booking.getStatus()
                    + ". Only APPROVED bookings can be issued.");
            return;
        }

        Equipment equipment = findEquipmentById(booking.getEquipmentId());
        if (equipment == null) {
            System.out.println("Error: the equipment record of this booking is missing.");
            return;
        }
        if (equipment.getStatus() == EquipmentStatus.DAMAGED
                || equipment.getStatus() == EquipmentStatus.UNDER_MAINTENANCE) {
            System.out.println("Error: the equipment is " + equipment.getStatus()
                    + " and cannot be issued.");
            return;
        }

        booking.setStatus(BookingStatus.ACTIVE);
        equipment.setStatus(EquipmentStatus.BOOKED);
        System.out.println("Equipment " + equipment.getName() + " issued to "
                + memberNameOf(booking.getMemberId()) + ".");
        System.out.println("Please return it on or before " + booking.getExpectedReturnDate() + ".");
    }

    private void returnEquipment() {
        System.out.println("\n----- Return Equipment -----");
        ArrayList<Booking> issued = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.ACTIVE
                    || booking.getStatus() == BookingStatus.OVERDUE) {
                issued.add(booking);
            }
        }
        if (issued.isEmpty()) {
            System.out.println("There is no issued equipment to return.");
            return;
        }
        printBookings(issued);

        String id = input.readId("Enter booking ID being returned: ");
        Booking booking = findBookingById(id);
        if (booking == null) {
            System.out.println("No booking found with ID " + id + ".");
            return;
        }
        // Business rule 9: only issued (ACTIVE or late ACTIVE = OVERDUE) bookings can be returned.
        if (booking.getStatus() != BookingStatus.ACTIVE && booking.getStatus() != BookingStatus.OVERDUE) {
            System.out.println("Error: booking " + id + " is " + booking.getStatus()
                    + ", so nothing can be returned.");
            return;
        }

        LocalDate returnDate = input.readDate("Actual return date (YYYY-MM-DD): ");
        if (returnDate.isBefore(booking.getStartDate())) {
            System.out.println("Error: the return date cannot be earlier than the start date ("
                    + booking.getStartDate() + ").");
            return;
        }

        int conditionChoice = input.readInt(
                "Condition after return (1 = Good, 2 = Damaged, 3 = Needs maintenance): ", 1, 3);

        Equipment equipment = findEquipmentById(booking.getEquipmentId());
        booking.setActualReturnDate(returnDate);
        booking.setStatus(BookingStatus.RETURNED);

        if (conditionChoice == 1) {
            booking.setReturnCondition("Good");
            if (equipment != null) {
                equipment.setStatus(EquipmentStatus.AVAILABLE);
                equipment.setCondition("Good");
            }
            System.out.println("Equipment returned in good condition and is available again.");
        } else {
            String notes = input.readNonEmptyText("Damage / problem notes: ");
            booking.setDamageNotes(notes);

            if (conditionChoice == 2) {
                booking.setReturnCondition("Damaged");
                if (equipment != null) {
                    equipment.setStatus(EquipmentStatus.DAMAGED);
                    equipment.setCondition("Poor");
                }
                addMaintenanceRecord(booking.getEquipmentId(), returnDate, "DAMAGE_REPORTED", notes);
                System.out.println("Equipment marked as DAMAGED. It cannot be booked until repaired.");
            } else {
                booking.setReturnCondition("Needs maintenance");
                if (equipment != null) {
                    equipment.setStatus(EquipmentStatus.UNDER_MAINTENANCE);
                    equipment.setCondition("Average");
                }
                addMaintenanceRecord(booking.getEquipmentId(), returnDate, "SENT_FOR_MAINTENANCE", notes);
                System.out.println("Equipment sent for maintenance.");
            }
        }

        if (returnDate.isAfter(booking.getExpectedReturnDate())) {
            System.out.println("Note: this item was returned late (due on "
                    + booking.getExpectedReturnDate() + ").");
        }
        System.out.println("Booking " + id + " is now RETURNED.");
    }

    /** Makes the equipment available again, unless it is damaged or under maintenance. */
    private void releaseEquipment(String equipmentId) {
        Equipment equipment = findEquipmentById(equipmentId);
        if (equipment != null && equipment.getStatus() == EquipmentStatus.BOOKED) {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
        }
    }

    /**
     * Business rule 11: an issued booking that has passed its due date becomes OVERDUE.
     * This method runs every time the main menu is shown.
     */
    private void updateOverdueBookings() {
        LocalDate today = LocalDate.now();
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.ACTIVE
                    && booking.getExpectedReturnDate().isBefore(today)) {
                booking.setStatus(BookingStatus.OVERDUE);
            }
        }
    }

    // =====================================================================
    // 12. Maintenance
    // =====================================================================

    private void manageMaintenance() {
        System.out.println("\n----- Maintenance -----");
        System.out.println("1. Send equipment for maintenance");
        System.out.println("2. Mark maintenance as completed (make available)");
        System.out.println("3. View maintenance history");
        System.out.println("4. Back to main menu");
        int choice = input.readInt("Choice: ", 1, 4);

        switch (choice) {
            case 1 -> sendForMaintenance();
            case 2 -> completeMaintenance();
            case 3 -> viewMaintenanceHistory();
            case 4 -> { /* go back */ }
        }
    }

    private void sendForMaintenance() {
        printEquipment(equipmentList);
        if (equipmentList.isEmpty()) {
            return;
        }
        String id = input.readId("Enter equipment ID to send for maintenance: ");
        Equipment equipment = findEquipmentById(id);
        if (equipment == null) {
            System.out.println("No equipment found with ID " + id + ".");
            return;
        }
        if (equipment.getStatus() == EquipmentStatus.BOOKED) {
            System.out.println("Error: this equipment is currently issued to a member.");
            return;
        }
        if (equipment.getStatus() == EquipmentStatus.UNDER_MAINTENANCE) {
            System.out.println("This equipment is already under maintenance.");
            return;
        }

        String notes = input.readNonEmptyText("Reason / notes: ");
        equipment.setStatus(EquipmentStatus.UNDER_MAINTENANCE);
        addMaintenanceRecord(id, LocalDate.now(), "SENT_FOR_MAINTENANCE", notes);
        System.out.println(equipment.getName() + " is now UNDER_MAINTENANCE.");
    }

    private void completeMaintenance() {
        ArrayList<Equipment> repairable = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            if (equipment.getStatus() == EquipmentStatus.UNDER_MAINTENANCE
                    || equipment.getStatus() == EquipmentStatus.DAMAGED) {
                repairable.add(equipment);
            }
        }
        if (repairable.isEmpty()) {
            System.out.println("No equipment is damaged or under maintenance right now.");
            return;
        }
        printEquipment(repairable);

        String id = input.readId("Enter equipment ID that is repaired: ");
        Equipment equipment = findEquipmentById(id);
        if (equipment == null) {
            System.out.println("No equipment found with ID " + id + ".");
            return;
        }
        if (equipment.getStatus() != EquipmentStatus.UNDER_MAINTENANCE
                && equipment.getStatus() != EquipmentStatus.DAMAGED) {
            System.out.println("Error: this equipment is " + equipment.getStatus()
                    + ", so maintenance cannot be completed for it.");
            return;
        }

        String notes = input.readNonEmptyText("Work done / notes: ");
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipment.setCondition("Good");
        addMaintenanceRecord(id, LocalDate.now(), "MAINTENANCE_COMPLETED", notes);
        System.out.println(equipment.getName() + " is repaired and AVAILABLE again.");
    }

    private void viewMaintenanceHistory() {
        if (maintenanceRecords.isEmpty()) {
            System.out.println("The maintenance history is empty.");
            return;
        }
        System.out.println("\n" + String.format("%-8s %-8s %-11s %-22s %s",
                "REC ID", "EQUIP", "DATE", "ACTION", "NOTES"));
        System.out.println("-------------------------------------------------------------------------------");
        for (MaintenanceRecord record : maintenanceRecords) {
            System.out.println(record);
        }
        System.out.println("Total records: " + maintenanceRecords.size());
    }

    private void addMaintenanceRecord(String equipmentId, LocalDate date, String action, String notes) {
        MaintenanceRecord record = new MaintenanceRecord(nextId("MNT", maintenanceCounter),
                equipmentId, date, action, notes);
        maintenanceCounter++;
        maintenanceRecords.add(record);
    }

    // =====================================================================
    // 13. Reports
    // =====================================================================

    private void viewReports() {
        updateOverdueBookings();

        int available = countEquipmentByStatus(EquipmentStatus.AVAILABLE);
        int booked = countEquipmentByStatus(EquipmentStatus.BOOKED);
        int damaged = countEquipmentByStatus(EquipmentStatus.DAMAGED);
        int maintenance = countEquipmentByStatus(EquipmentStatus.UNDER_MAINTENANCE);

        System.out.println("\n==================================================");
        System.out.println("                CLUBGEAR REPORTS");
        System.out.println("            Generated on " + LocalDate.now());
        System.out.println("==================================================");
        System.out.println("Total clubs                : " + clubs.size());
        System.out.println("Total registered members   : " + members.size());
        System.out.println("Total equipment items      : " + equipmentList.size());
        System.out.println("Available equipment        : " + available);
        System.out.println("Currently booked equipment : " + booked);
        System.out.println("Damaged equipment          : " + damaged);
        System.out.println("Equipment under maintenance: " + maintenance);
        System.out.println("Total bookings made        : " + bookings.size());
        System.out.println("Completed (returned)       : " + filterByStatus(BookingStatus.RETURNED).size());
        System.out.println("Cancelled bookings         : " + filterByStatus(BookingStatus.CANCELLED).size());
        System.out.println("Overdue bookings           : " + filterByStatus(BookingStatus.OVERDUE).size());

        printOverdueDetails();
        printMostBookedEquipment();
        printClubWiseUsage();
        printMaintenanceList();
        System.out.println("==================================================");
    }

    private int countEquipmentByStatus(EquipmentStatus status) {
        int count = 0;
        for (Equipment equipment : equipmentList) {
            if (equipment.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    private void printOverdueDetails() {
        ArrayList<Booking> overdue = filterByStatus(BookingStatus.OVERDUE);
        System.out.println("\n--- Overdue bookings ---");
        if (overdue.isEmpty()) {
            System.out.println("None. Every issued item is still within its date.");
            return;
        }
        for (Booking booking : overdue) {
            long lateDays = LocalDate.now().toEpochDay() - booking.getExpectedReturnDate().toEpochDay();
            System.out.println(booking.getBookingId() + " | " + memberNameOf(booking.getMemberId())
                    + " | " + equipmentNameOf(booking.getEquipmentId())
                    + " | due " + booking.getExpectedReturnDate()
                    + " | late by " + lateDays + " day(s)");
        }
    }

    /** Uses a HashMap to count how many times each equipment item was booked. */
    private void printMostBookedEquipment() {
        System.out.println("\n--- Most frequently booked equipment ---");
        if (bookings.isEmpty()) {
            System.out.println("No bookings have been made yet.");
            return;
        }

        HashMap<String, Integer> counts = new HashMap<>();
        for (Booking booking : bookings) {
            String key = booking.getEquipmentId();
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }

        String topId = null;
        int topCount = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + " - " + equipmentNameOf(entry.getKey())
                    + " : " + entry.getValue() + " booking(s)");
            if (entry.getValue() > topCount) {
                topCount = entry.getValue();
                topId = entry.getKey();
            }
        }
        if (topId != null) {
            System.out.println("Most booked item: " + equipmentNameOf(topId)
                    + " (" + topId + ") with " + topCount + " booking(s).");
        }
    }

    /** Uses a HashMap to count bookings club by club. */
    private void printClubWiseUsage() {
        System.out.println("\n--- Club-wise equipment usage ---");
        if (clubs.isEmpty()) {
            System.out.println("No clubs added yet.");
            return;
        }

        HashMap<String, Integer> usage = new HashMap<>();
        for (Club club : clubs) {
            usage.put(club.getClubId(), 0);
        }
        for (Booking booking : bookings) {
            String clubId = booking.getClubId();
            usage.put(clubId, usage.getOrDefault(clubId, 0) + 1);
        }

        for (Club club : clubs) {
            int ownedItems = 0;
            for (Equipment equipment : equipmentList) {
                if (equipment.getOwnerClubId().equals(club.getClubId())) {
                    ownedItems++;
                }
            }
            System.out.println(club.getName() + " (" + club.getClubId() + ") : "
                    + usage.getOrDefault(club.getClubId(), 0) + " booking(s), "
                    + ownedItems + " item(s) owned");
        }
    }

    private void printMaintenanceList() {
        System.out.println("\n--- Equipment needing attention ---");
        boolean found = false;
        for (Equipment equipment : equipmentList) {
            if (equipment.getStatus() == EquipmentStatus.DAMAGED
                    || equipment.getStatus() == EquipmentStatus.UNDER_MAINTENANCE) {
                System.out.println(equipment.getEquipmentId() + " - " + equipment.getName()
                        + " : " + equipment.getStatus());
                found = true;
            }
        }
        if (!found) {
            System.out.println("All equipment is in working condition.");
        }
    }

    // =====================================================================
    // 14 & 15. Saving and loading
    // =====================================================================

    private void saveData() {
        ArrayList<String> clubLines = new ArrayList<>();
        for (Club club : clubs) {
            clubLines.add(club.toCsvLine());
        }
        ArrayList<String> memberLines = new ArrayList<>();
        for (Member member : members) {
            memberLines.add(member.toCsvLine());
        }
        ArrayList<String> equipmentLines = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            equipmentLines.add(equipment.toCsvLine());
        }
        ArrayList<String> bookingLines = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingLines.add(booking.toCsvLine());
        }
        ArrayList<String> maintenanceLines = new ArrayList<>();
        for (MaintenanceRecord record : maintenanceRecords) {
            maintenanceLines.add(record.toCsvLine());
        }

        boolean ok = storage.writeLines(FileStorage.CLUBS_FILE,
                "clubId,name,coordinatorName,category", clubLines);
        ok = storage.writeLines(FileStorage.MEMBERS_FILE,
                "memberId,name,email,course,year,clubId", memberLines) && ok;
        ok = storage.writeLines(FileStorage.EQUIPMENT_FILE,
                "equipmentId,name,category,quantity,condition,ownerClubId,status", equipmentLines) && ok;
        ok = storage.writeLines(FileStorage.BOOKINGS_FILE,
                "bookingId,memberId,equipmentId,clubId,startDate,expectedReturnDate,actualReturnDate,"
                        + "purpose,status,createdAt,returnCondition,damageNotes", bookingLines) && ok;
        ok = storage.writeLines(FileStorage.MAINTENANCE_FILE,
                "recordId,equipmentId,recordDate,action,notes", maintenanceLines) && ok;

        if (ok) {
            System.out.println("\nData saved successfully inside the '"
                    + FileStorage.DATA_FOLDER + "' folder.");
            System.out.println("Clubs: " + clubs.size() + ", Members: " + members.size()
                    + ", Equipment: " + equipmentList.size() + ", Bookings: " + bookings.size()
                    + ", Maintenance records: " + maintenanceRecords.size());
        } else {
            System.out.println("\nSome files could not be saved. Please check folder permissions.");
        }
    }

    private void loadData() {
        ArrayList<String> clubLines = storage.readLines(FileStorage.CLUBS_FILE);
        ArrayList<String> memberLines = storage.readLines(FileStorage.MEMBERS_FILE);
        ArrayList<String> equipmentLines = storage.readLines(FileStorage.EQUIPMENT_FILE);
        ArrayList<String> bookingLines = storage.readLines(FileStorage.BOOKINGS_FILE);
        ArrayList<String> maintenanceLines = storage.readLines(FileStorage.MAINTENANCE_FILE);

        if (clubLines.isEmpty() && memberLines.isEmpty() && equipmentLines.isEmpty()
                && bookingLines.isEmpty() && maintenanceLines.isEmpty()) {
            System.out.println("\nNo saved data was found, so nothing was loaded.");
            System.out.println("(The current data in memory has not been changed.)");
            return;
        }

        // Clear everything first so that loading twice does not duplicate records.
        clubs.clear();
        members.clear();
        equipmentList.clear();
        bookings.clear();
        maintenanceRecords.clear();
        registeredEmails.clear();

        int skipped = 0;

        for (String line : clubLines) {
            Club club = Club.fromCsvLine(line);
            if (club == null) {
                skipped++;
            } else {
                clubs.add(club);
            }
        }
        for (String line : memberLines) {
            Member member = Member.fromCsvLine(line);
            if (member == null) {
                skipped++;
            } else {
                members.add(member);
                registeredEmails.add(member.getEmail());
            }
        }
        for (String line : equipmentLines) {
            Equipment equipment = Equipment.fromCsvLine(line);
            if (equipment == null) {
                skipped++;
            } else {
                equipmentList.add(equipment);
            }
        }
        for (String line : bookingLines) {
            Booking booking = Booking.fromCsvLine(line);
            if (booking == null) {
                skipped++;
            } else {
                bookings.add(booking);
            }
        }
        for (String line : maintenanceLines) {
            MaintenanceRecord record = MaintenanceRecord.fromCsvLine(line);
            if (record == null) {
                skipped++;
            } else {
                maintenanceRecords.add(record);
            }
        }

        refreshCounters();
        updateOverdueBookings();

        System.out.println("\nData loaded successfully.");
        System.out.println("Clubs: " + clubs.size() + ", Members: " + members.size()
                + ", Equipment: " + equipmentList.size() + ", Bookings: " + bookings.size()
                + ", Maintenance records: " + maintenanceRecords.size());
        if (skipped > 0) {
            System.out.println("Note: " + skipped + " damaged line(s) in the files were skipped.");
        }
    }

    /** After loading, the counters must continue from the biggest ID already used. */
    private void refreshCounters() {
        clubCounter = 1;
        for (Club club : clubs) {
            clubCounter = Math.max(clubCounter, idNumber(club.getClubId()) + 1);
        }
        memberCounter = 1;
        for (Member member : members) {
            memberCounter = Math.max(memberCounter, idNumber(member.getMemberId()) + 1);
        }
        equipmentCounter = 1;
        for (Equipment equipment : equipmentList) {
            equipmentCounter = Math.max(equipmentCounter, idNumber(equipment.getEquipmentId()) + 1);
        }
        bookingCounter = 1;
        for (Booking booking : bookings) {
            bookingCounter = Math.max(bookingCounter, idNumber(booking.getBookingId()) + 1);
        }
        maintenanceCounter = 1;
        for (MaintenanceRecord record : maintenanceRecords) {
            maintenanceCounter = Math.max(maintenanceCounter, idNumber(record.getRecordId()) + 1);
        }
    }

    /** Takes the number part of an ID, for example "CLB007" gives 7. */
    private int idNumber(String id) {
        try {
            return Integer.parseInt(id.substring(3));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return 0;
        }
    }

    // =====================================================================
    // Sample data (used the first time, when no files are saved yet)
    // =====================================================================

    private void loadSampleData() {
        LocalDate today = LocalDate.now();

        clubs.add(new Club("CLB001", "Photography Club", "Prof. Anita Sharma", "Cultural"));
        clubs.add(new Club("CLB002", "Coding Club", "Prof. Ravi Menon", "Technical"));
        clubCounter = 3;

        addSampleMember(new Member("MEM001", "Aditya Verma", "aditya.verma@college.edu", "CSE", 2, "CLB001"));
        addSampleMember(new Member("MEM002", "Sneha Patil", "sneha.patil@college.edu", "IT", 3, "CLB002"));
        addSampleMember(new Member("MEM003", "Rahul Nair", "rahul.nair@college.edu", "ECE", 2, "CLB001"));
        memberCounter = 4;

        equipmentList.add(new Equipment("EQP001", "Canon DSLR Camera", "Camera", 2, "Good",
                "CLB001", EquipmentStatus.AVAILABLE));
        equipmentList.add(new Equipment("EQP002", "Wireless Microphone", "Audio", 4, "Good",
                "CLB002", EquipmentStatus.BOOKED));
        equipmentList.add(new Equipment("EQP003", "Epson Projector", "Display", 1, "Good",
                "CLB002", EquipmentStatus.AVAILABLE));
        equipmentList.add(new Equipment("EQP004", "Camera Tripod", "Camera", 3, "Average",
                "CLB001", EquipmentStatus.AVAILABLE));
        equipmentList.add(new Equipment("EQP005", "LED Light Panel", "Lighting", 2, "Poor",
                "CLB001", EquipmentStatus.DAMAGED));
        equipmentCounter = 6;

        // Booking 1: approved and waiting to be issued (good for the "Issue" demo).
        Booking b1 = new Booking("BKG001", "MEM001", "EQP001", "CLB001",
                today.plusDays(1), today.plusDays(3), "Annual day photo coverage",
                BookingStatus.APPROVED, LocalDateTime.now());
        bookings.add(b1);

        // Booking 2: already issued and late, so it will be detected as OVERDUE.
        Booking b2 = new Booking("BKG002", "MEM002", "EQP002", "CLB002",
                today.minusDays(6), today.minusDays(1), "Tech talk audio setup",
                BookingStatus.ACTIVE, LocalDateTime.now());
        bookings.add(b2);

        // Booking 3: finished booking, useful for the reports.
        Booking b3 = new Booking("BKG003", "MEM003", "EQP003", "CLB002",
                today.minusDays(20), today.minusDays(18), "Seminar presentation",
                BookingStatus.RETURNED, LocalDateTime.now());
        b3.setActualReturnDate(today.minusDays(18));
        b3.setReturnCondition("Good");
        bookings.add(b3);
        bookingCounter = 4;

        maintenanceRecords.add(new MaintenanceRecord("MNT001", "EQP005", today.minusDays(5),
                "DAMAGE_REPORTED", "Light panel flickers after the cultural event"));
        maintenanceCounter = 2;

        System.out.println("Sample data loaded: " + clubs.size() + " clubs, " + members.size()
                + " members, " + equipmentList.size() + " equipment items, "
                + bookings.size() + " bookings.");
    }

    private void addSampleMember(Member member) {
        members.add(member);
        registeredEmails.add(member.getEmail());
    }

    // =====================================================================
    // Small helper methods used everywhere
    // =====================================================================

    /** Builds an ID such as CLB001, MEM012 or BKG105. */
    private String nextId(String prefix, int number) {
        return String.format("%s%03d", prefix, number);
    }

    private Club findClubById(String clubId) {
        for (Club club : clubs) {
            if (club.getClubId().equalsIgnoreCase(clubId)) {
                return club;
            }
        }
        return null;
    }

    private Club findClubByName(String name) {
        for (Club club : clubs) {
            if (club.getName().equalsIgnoreCase(name)) {
                return club;
            }
        }
        return null;
    }

    private Member findMemberById(String memberId) {
        for (Member member : members) {
            if (member.getMemberId().equalsIgnoreCase(memberId)) {
                return member;
            }
        }
        return null;
    }

    private Equipment findEquipmentById(String equipmentId) {
        for (Equipment equipment : equipmentList) {
            if (equipment.getEquipmentId().equalsIgnoreCase(equipmentId)) {
                return equipment;
            }
        }
        return null;
    }

    private Booking findBookingById(String bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId().equalsIgnoreCase(bookingId)) {
                return booking;
            }
        }
        return null;
    }

    private String clubNameOf(String clubId) {
        Club club = findClubById(clubId);
        return (club == null) ? "Unknown club" : club.getName();
    }

    private String memberNameOf(String memberId) {
        Member member = findMemberById(memberId);
        return (member == null) ? "Unknown member" : member.getName();
    }

    private String equipmentNameOf(String equipmentId) {
        Equipment equipment = findEquipmentById(equipmentId);
        return (equipment == null) ? "Unknown equipment" : equipment.getName();
    }
}
