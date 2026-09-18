# ClubGear Test Plan

**Module under test:** ClubGear console application
**Tested by:** _______________
**Date of testing:** _______________
**Environment:** JDK 17 or later, Windows / Linux command prompt

## How to run these tests

1. Delete the `data` folder if it exists, so that the built-in sample data is loaded.
2. Compile: `javac -d out src/clubgear/*.java`
3. Run: `java -cp out clubgear.Main`
4. Execute the test cases in the given order (TC-04 onwards depend on earlier steps).
5. Fill the "Actual output" and "Result" columns while testing.

**Sample data used as the starting point**

| Data | Value |
|------|-------|
| Clubs | CLB001 Photography Club, CLB002 Coding Club |
| Members | MEM001 Aditya Verma, MEM002 Sneha Patil, MEM003 Rahul Nair |
| Equipment | EQP001 Canon DSLR Camera (AVAILABLE), EQP002 Wireless Microphone (BOOKED), EQP003 Epson Projector (AVAILABLE), EQP004 Camera Tripod (AVAILABLE), EQP005 LED Light Panel (DAMAGED) |
| Bookings | BKG001 APPROVED, BKG002 issued and late, BKG003 RETURNED |

In the table below, **T** means today's date, **T+1** means tomorrow, and so on.

---

## Test Cases

### TC-01 — Duplicate club name is rejected

| Field | Detail |
|-------|--------|
| **Description** | The system must not allow two clubs with the same name (business rule 1). |
| **Input** | Menu `1`, club name `Photography Club` |
| **Expected output** | `Error: a club named 'Photography Club' already exists.` and no new club is created. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-02 — Valid club is added with a unique ID

| Field | Detail |
|-------|--------|
| **Description** | A new club gets the next auto generated ID. |
| **Input** | Menu `1`, name `Robotics Club`, coordinator `Prof. Iqbal Khan`, category `Technical` |
| **Expected output** | `Club added successfully. Club ID: CLB003` and the club appears under menu `2` → `1`. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-03 — Duplicate member email is rejected

| Field | Detail |
|-------|--------|
| **Description** | One email address can be registered only once (business rule 2). |
| **Input** | Menu `3` → `1`, name `Aditya Verma`, email `aditya.verma@college.edu` |
| **Expected output** | `Error: this email is already registered.` and no new member is created. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-04 — Invalid email format is rejected

| Field | Detail |
|-------|--------|
| **Description** | The email must contain one `@` and a dot in the domain. |
| **Input** | Menu `3` → `1`, name `Test Student`, email `rahulcollege.edu` |
| **Expected output** | `Invalid email format. Example: student@college.edu` and the prompt is repeated. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-05 — Invalid equipment quantity is rejected

| Field | Detail |
|-------|--------|
| **Description** | Equipment quantity must be greater than zero (business rule 3). |
| **Input** | Menu `4`, name `New Speaker`, category `Audio`, quantity `0` |
| **Expected output** | `Please enter a number between 1 and 500.` and the quantity prompt is repeated until a valid number is typed. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-06 — Successful booking creation

| Field | Detail |
|-------|--------|
| **Description** | A booking of free equipment for valid dates is accepted. |
| **Input** | Menu `6`, member `MEM003`, equipment `EQP004`, club `CLB001`, start `T+4`, return `T+6`, purpose `Fest photography` |
| **Expected output** | A `BOOKING CONFIRMATION` block showing booking ID `BKG004` with status `REQUESTED`. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-07 — Overlapping booking of the same equipment is rejected

| Field | Detail |
|-------|--------|
| **Description** | Two live bookings of one item must not overlap (business rules 5 and 6). |
| **Input** | Menu `6`, member `MEM001`, equipment `EQP004`, club `CLB001`, start `T+5`, return `T+7` |
| **Expected output** | `Error: this equipment is already booked from T+4 to T+6 (booking BKG004).` and no booking is created. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-08 — Non-overlapping booking of the same equipment is accepted

| Field | Detail |
|-------|--------|
| **Description** | The same item can be booked again for a later free period. |
| **Input** | Menu `6`, member `MEM001`, equipment `EQP004`, club `CLB001`, start `T+8`, return `T+9`, purpose `Club shoot` |
| **Expected output** | A confirmation block with booking ID `BKG005`, status `REQUESTED`. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-09 — Booking damaged equipment is rejected

| Field | Detail |
|-------|--------|
| **Description** | Damaged or under maintenance items cannot be booked (business rule 4). |
| **Input** | Menu `6`, member `MEM001`, equipment `EQP005` |
| **Expected output** | `Error: this equipment is DAMAGED and cannot be booked right now.` |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-10 — Return date earlier than start date is rejected

| Field | Detail |
|-------|--------|
| **Description** | The return date must not be before the start date (business rule 7). |
| **Input** | Menu `6`, member `MEM001`, equipment `EQP001`, club `CLB001`, start `T+10`, return `T+9` |
| **Expected output** | `Error: the return date cannot be earlier than the start date.` |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-11 — Badly formatted date is rejected without crashing

| Field | Detail |
|-------|--------|
| **Description** | A wrong date format must be caught by exception handling. |
| **Input** | Menu `6`, member `MEM001`, equipment `EQP001`, club `CLB001`, start `12-09-2026` |
| **Expected output** | `Invalid date. Please use the format YYYY-MM-DD (example: ...)` and the prompt is repeated. The program keeps running. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-12 — Booking approval

| Field | Detail |
|-------|--------|
| **Description** | A REQUESTED booking becomes APPROVED. |
| **Input** | Menu `8`, booking `BKG004` |
| **Expected output** | `Booking BKG004 approved. It can now be issued (menu option 10).` Status shown as APPROVED under menu `7` → `1`. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-13 — Booking cancellation releases the equipment

| Field | Detail |
|-------|--------|
| **Description** | A requested or approved booking can be cancelled (business rule 10). |
| **Input** | Menu `9`, booking `BKG005` |
| **Expected output** | `Booking BKG005 has been cancelled.` and the status becomes CANCELLED. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-14 — Equipment issue (invalid status change is blocked)

| Field | Detail |
|-------|--------|
| **Description** | Only an APPROVED booking can be issued (business rule 8). |
| **Input** | Menu `10`, booking `BKG005` (already cancelled), then booking `BKG004` |
| **Expected output** | For BKG005: `Error: booking BKG005 is CANCELLED. Only APPROVED bookings can be issued.` For BKG004: `Equipment Camera Tripod issued to Rahul Nair.` and EQP004 becomes BOOKED. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-15 — Equipment return in good condition

| Field | Detail |
|-------|--------|
| **Description** | An ACTIVE booking is returned and the item becomes available again (business rules 9 and 10). |
| **Input** | Menu `11`, booking `BKG004`, return date `T+6`, condition `1` |
| **Expected output** | `Equipment returned in good condition and is available again.` and `Booking BKG004 is now RETURNED.` EQP004 shows AVAILABLE under menu `5` → `1`. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-16 — Return with damage creates a maintenance record

| Field | Detail |
|-------|--------|
| **Description** | A damaged return marks the item DAMAGED and writes a maintenance history line. |
| **Input** | Menu `11`, booking `BKG002`, return date `T`, condition `2`, notes `Battery cover broken` |
| **Expected output** | `Equipment marked as DAMAGED. It cannot be booked until repaired.` A new record appears under menu `12` → `3` with action `DAMAGE_REPORTED`. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-17 — Overdue booking detection

| Field | Detail |
|-------|--------|
| **Description** | An issued booking past its due date becomes OVERDUE (business rule 11). Run this test **before** TC-16, because TC-16 returns the overdue item. |
| **Input** | Menu `13` (View Reports) on freshly loaded sample data |
| **Expected output** | `Overdue bookings : 1` and a line `BKG002 \| Sneha Patil \| Wireless Microphone \| due T-1 \| late by 1 day(s)`. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-18 — Maintenance status change

| Field | Detail |
|-------|--------|
| **Description** | An item can be sent for maintenance and later made available again. |
| **Input** | Menu `12` → `1`, equipment `EQP003`, notes `Lamp needs service`; then menu `12` → `2`, equipment `EQP003`, notes `Lamp replaced` |
| **Expected output** | First: `Epson Projector is now UNDER_MAINTENANCE.` Then: `Epson Projector is repaired and AVAILABLE again.` Both actions appear in the maintenance history. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-19 — Invalid menu input does not crash the program

| Field | Detail |
|-------|--------|
| **Description** | Letters and out of range numbers must be handled (business rule 12). |
| **Input** | At the main menu type `abc`, then `0`, then `99` |
| **Expected output** | `That is not a valid number. Please try again.` then `Please enter a number between 1 and 16.` twice. The menu keeps working. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-20 — Save data

| Field | Detail |
|-------|--------|
| **Description** | All five lists are written to CSV files. |
| **Input** | Menu `14` |
| **Expected output** | `Data saved successfully inside the 'data' folder.` with the record counts. The files `clubs.csv`, `members.csv`, `equipment.csv`, `bookings.csv` and `maintenance.csv` exist inside `data`. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-21 — Load data after restart

| Field | Detail |
|-------|--------|
| **Description** | Saved data comes back when the program is started again. |
| **Input** | Exit with `16`, start the program again, then menu `2` → `1` and `7` → `1` |
| **Expected output** | At start up: `Saved data found. Loading it now...` followed by `Data loaded successfully.` with the same counts as TC-20. The club added in TC-02 and the booking from TC-15 are still present. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

### TC-22 — Missing data files handled gracefully

| Field | Detail |
|-------|--------|
| **Description** | A missing `data` folder must not crash the program (file handling requirement). |
| **Input** | Delete the `data` folder, start the program, choose menu `15` |
| **Expected output** | At start up: `No saved data found in the 'data' folder.` and the sample data is loaded. On menu `15`: `No saved data was found, so nothing was loaded.` No exception stack trace appears. |
| **Actual output** | |
| **Result (Pass/Fail)** | |

---

## Test Summary

| Total test cases | Passed | Failed | Not executed |
|------------------|--------|--------|--------------|
| 22 | | | |

**Tester remarks:**

_________________________________________________________________

_________________________________________________________________
