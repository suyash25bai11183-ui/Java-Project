# ClubGear: A Campus Club Equipment Booking and Management System

A console based Java application that helps college clubs keep track of the equipment they
own and the members who borrow it.

**Course project (flipped classroom):** Java programming, second year B.Tech Computer Science
**Type:** Console application, core Java only, no frameworks and no database

---

## Overview

In most colleges, clubs own useful equipment such as cameras, microphones, projectors,
tripods and lighting panels. This equipment is usually tracked in a notebook kept in the club
room or in a WhatsApp group, which causes three problems again and again:

1. Two members ask for the same camera on the same day and the clash is discovered on the
   morning of the event.
2. Nobody remembers who took the microphone last week.
3. Damaged items are issued again because the damage was never written down.

ClubGear replaces that notebook. Every club, member, equipment item, booking and maintenance
entry gets a unique ID, and the program refuses any action that breaks a booking rule —
a double booking, a booking of damaged equipment, or a return date earlier than the start
date. All data is saved to plain CSV files, so it survives between runs.

The system has **seven functional modules** (clubs, members, equipment, booking, booking
status, return and maintenance, reports), which is above the minimum of three required by the
course guidelines.

---

## Features

**Club management**
- Add a club with an automatically generated ID (CLB001, CLB002, ...)
- Store club name, coordinator name and category
- View all clubs, search by name or by ID
- Duplicate club names are rejected

**Member management**
- Register a member with an auto generated ID (MEM001, MEM002, ...)
- Store name, email, course, year of study and club
- Duplicate emails are rejected using a HashSet; the email format is validated
- View all members, search by name or ID

**Equipment management**
- Add equipment with an auto generated ID (EQP001, EQP002, ...)
- Store name, category, quantity, condition, owning club and status
- Quantity must be greater than zero
- Status is one of AVAILABLE, BOOKED, DAMAGED, UNDER_MAINTENANCE
- View all equipment, search by name or category, update condition

**Booking**
- Create a booking by choosing member, equipment, club, dates and purpose
- Auto generated booking ID (BKG001, BKG002, ...)
- Overlapping bookings for the same item are blocked
- Damaged or under maintenance equipment cannot be booked
- The return date can never be earlier than the start date
- A booking confirmation slip is printed on the screen

**Booking status flow**
- REQUESTED, APPROVED, ACTIVE, RETURNED, CANCELLED, OVERDUE
- Approve a requested booking; cancel a requested or approved booking
- Issue equipment (APPROVED becomes ACTIVE)
- Return equipment (ACTIVE or OVERDUE becomes RETURNED)
- Overdue bookings are detected automatically against today's date
- Invalid status changes are refused with a message naming the current state

**Return and maintenance**
- Record the actual return date and the condition after return
- Add damage notes when something is broken
- Send equipment for maintenance and mark maintenance as completed
- A maintenance history is kept for every item

**Reports**
- Total clubs, members, equipment items and bookings
- Available, booked, damaged and under maintenance counts
- Overdue bookings with the number of late days
- Most frequently booked equipment (computed with a HashMap)
- Club-wise equipment usage
- Completed and cancelled bookings

**File handling**
- Save and load all five data types as CSV text files inside a `data` folder
- Missing files are handled gracefully; the program never crashes
- Damaged lines in a file are skipped with a count, not treated as a fatal error
- Saved data is loaded automatically when the program starts

---

## Technologies Used

| Item | Detail |
|------|--------|
| Language | Java 17 (compiled with `--release 17`, runs on Java 17 and above) |
| Input | `java.util.Scanner` |
| Collections | `ArrayList`, `HashMap`, `HashSet` |
| Dates | `java.time.LocalDate`, `java.time.LocalDateTime` |
| Files | `java.io.BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`, `File` |
| Storage | Plain CSV text files, no database |
| Testing | Self-written validation test suite (no JUnit, no external library) |
| External libraries | None |
| Build tool | Plain `javac`, no Maven or Gradle |
| Version control | Git and GitHub |

---

## Folder Structure

```
ClubGear/
├── src/
│   └── clubgear/
│       ├── Main.java               // program entry point
│       ├── ClubGearSystem.java     // menu and all operations
│       ├── Club.java               // club data
│       ├── Member.java             // member data
│       ├── Equipment.java          // equipment data
│       ├── Booking.java            // booking data and overlap check
│       ├── MaintenanceRecord.java  // one maintenance history line
│       ├── BookingStatus.java      // enum of booking states
│       ├── EquipmentStatus.java    // enum of equipment states
│       ├── FileStorage.java        // CSV read and write
│       └── InputValidator.java     // safe keyboard input
├── tests/
│   ├── TestPlan.md                 // 22 manual test cases
│   └── clubgear/
│       └── ValidationTests.java    // 55 automated validation tests
├── docs/
│   ├── DESIGN.md                   // architecture, UML, ER and design rationale
│   ├── SCREENSHOTS.md              // real captured console output
│   └── GIT_GUIDE.md                // repository and commit guidance
├── data/                           // created automatically after the first save
│   ├── clubs.csv
│   ├── members.csv
│   ├── equipment.csv
│   ├── bookings.csv
│   └── maintenance.csv
├── ClubGear_Project_Report.pdf     // full report for the portal
├── README.md
├── statement.md
└── .gitignore
```

---

## How to Install and Run

### Requirement

JDK 17 or later. Check with:

```
java -version
javac -version
```

If `javac` is missing, install a JDK (for example Adoptium Temurin 17 or later).

### Step 1 — Get the code

```
git clone https://github.com/YOUR-USERNAME/clubgear.git
cd clubgear
```

### Step 2 — Compile

Linux or macOS:

```
javac -d out src/clubgear/*.java
```

Windows:

```
javac -d out src\clubgear\*.java
```

The compiled `.class` files go into the `out` folder. The project compiles with no errors and
no warnings, including with `-Xlint:all`.

### Step 3 — Run

Linux or macOS:

```
java -cp out clubgear.Main
```

Windows:

```
java -cp out clubgear.Main
```

The first run has no `data` folder, so ClubGear loads its built-in sample data (2 clubs,
3 members, 5 equipment items, 3 bookings). After you use option **14. Save Data**, the `data`
folder is created and every later run loads your saved data automatically.

---

## Instructions for Testing

### Automated validation tests

55 tests covering the date overlap logic, the status rules, quantity validation, email
validation, CSV conversion in both directions, damaged file lines and the file storage layer.

```
javac -d out src/clubgear/*.java
javac -d out-tests -cp out tests/clubgear/ValidationTests.java
java -cp out:out-tests clubgear.ValidationTests      # Linux / macOS
java -cp out;out-tests clubgear.ValidationTests      # Windows
```

Expected ending:

```
Total tests : 55
Passed      : 55
Failed      : 0
RESULT: ALL TESTS PASSED
```

The runner exits with code 0 when everything passes and 1 when anything fails, so it can be
used in a CI script later.

### Manual test plan

`tests/TestPlan.md` contains 22 manual test cases with test case ID, description, input,
expected output, and blank columns for actual output and result. They cover duplicate clubs,
duplicate emails, invalid quantity, successful booking, overlapping booking, booking damaged
equipment, invalid dates, approval, cancellation, issue, return, overdue detection,
maintenance, invalid menu input, save and load.

Run them in order after deleting the `data` folder, and fill in the two blank columns as you go.

---

## Sample Workflow

A five minute demonstration that touches every module:

1. Start the program. Sample data loads automatically.
2. Choose **13. View Reports** and notice one overdue booking (BKG002, the microphone).
3. Choose **6. Create Equipment Booking**: member `MEM003`, equipment `EQP004`, club `CLB001`,
   start date a few days ahead, return date two days later, purpose "Fest photography".
   A confirmation slip with booking ID `BKG004` appears.
4. Repeat step 3 with overlapping dates for `EQP004`. The booking is refused and the clashing
   booking is named.
5. Try to book `EQP005` (the damaged LED panel). The booking is refused.
6. Choose **8. Approve Booking** and approve `BKG004`.
7. Choose **10. Issue Equipment** and issue `BKG004`. The tripod becomes BOOKED.
8. Choose **11. Return Equipment**, return `BKG004` in good condition. The tripod becomes
   AVAILABLE again.
9. Choose **12. Manage Maintenance** to send an item for repair and then mark it repaired.
10. Choose **14. Save Data**, exit with **16**, run the program again and watch the data
    come back.

---

## Screenshots

Real captured console output for every step above is in
[docs/SCREENSHOTS.md](docs/SCREENSHOTS.md) — start-up, menu, booking confirmation, conflict
detection, the reports page, error handling and the saved CSV files.

---

## Design Documentation

[docs/DESIGN.md](docs/DESIGN.md) contains:

- System architecture diagram (three layers)
- Process flow diagram of the complete booking life cycle
- Use case diagram with an actor / pre-condition / post-condition table
- Class diagram of all 11 classes
- Sequence diagram of the booking creation flow
- ER diagram and the schema of all five CSV files
- Design decisions and the reasoning behind them

The full report with all of the above plus implementation details, testing approach,
challenges and learnings is in `ClubGear_Project_Report.pdf`.

---

## Limitations

These limitations are intentional, so that the project stays at second year level.

1. **Quantity is descriptive, not a live stock count.** One equipment record is treated as one
   bookable asset. If a club owns two cameras, add them as two separate records so both can be
   booked at the same time.
2. **Commas are not allowed inside typed text.** The CSV files use commas as separators, so
   `InputValidator.cleanText()` replaces any typed comma with a space before storing it.
3. **No login system.** Anyone using the program can act as any member or coordinator.
4. **Single user only.** The program is not designed for two people running it at once.
5. **Data lives in memory until saved.** The exit option asks whether to save, as a reminder.
6. **Overdue status is calculated only while the program is running**, because there is no
   background scheduler.

---

## Future Enhancements

1. A login screen with separate coordinator and member roles.
2. Real stock counting, so that 3 of 4 microphones can be booked at once.
3. Email or SMS reminders one day before the return date.
4. Fine calculation for late returns.
5. Booking history export as a printable report.
6. Replacing CSV files with SQLite through JDBC.
7. A JavaFX or Swing graphical interface over the same classes.
8. QR code stickers on equipment for faster issue and return.
