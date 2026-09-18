# ClubGear — Screenshots and Results

Every block on this page is real output captured from a live run of the compiled program on
16 September 2026. Nothing here is typed by hand or edited. To reproduce it, delete the
`data` folder and follow the sample workflow in the README.

If you are uploading this project to GitHub, take real terminal screenshots of the sections
below and place the image files in `docs/images/`, then replace each code block with
`![caption](images/filename.png)`. The text captures work as a fallback.

---

## 1. Program start-up (no saved data yet)

```
**************************************************
*                   CLUBGEAR                     *
*  Campus Club Equipment Booking and Management   *
**************************************************
No saved data found in the 'data' folder.
Loading built-in sample data for demonstration.
Sample data loaded: 2 clubs, 3 members, 5 equipment items, 3 bookings.
```

## 2. Main menu

```
==================================================
            CLUBGEAR  -  MAIN MENU
   Today's date: 2026-09-16
==================================================
 1. Add Club
 2. View / Search Clubs
 3. Register / View Members
 4. Add Equipment
 5. View / Search / Update Equipment
 6. Create Equipment Booking
 7. View Bookings
 8. Approve Booking
 9. Cancel Booking
10. Issue Equipment
11. Return Equipment
12. Manage Maintenance
13. View Reports
14. Save Data
15. Load Data
16. Exit
--------------------------------------------------
Enter your choice (1-16):
```

## 3. Equipment list (menu 5 → 1)

```
ID       NAME                     CATEGORY       QTY  CONDITION CLUB     STATUS
---------------------------------------------------------------------------------------
EQP001   Canon DSLR Camera        Camera         2    Good      CLB001   AVAILABLE
EQP002   Wireless Microphone      Audio          4    Good      CLB002   BOOKED
EQP003   Epson Projector          Display        1    Good      CLB002   AVAILABLE
EQP004   Camera Tripod            Camera         3    Average   CLB001   AVAILABLE
EQP005   LED Light Panel          Lighting       2    Poor      CLB001   DAMAGED
Total equipment shown: 5
```

## 4. Member list (menu 3 → 2)

```
ID       NAME                 EMAIL                        COURSE   YEAR  CLUB
---------------------------------------------------------------------------------
MEM001   Aditya Verma         aditya.verma@college.edu     CSE      2     CLB001
MEM002   Sneha Patil          sneha.patil@college.edu      IT       3     CLB002
MEM003   Rahul Nair           rahul.nair@college.edu       ECE      2     CLB001
Total members shown: 3
```

## 5. Successful booking (menu 6)

```
Enter member ID: MEM003
Enter equipment ID: EQP004
Booking for which club ID (usually the owner CLB001): CLB001
Booking start date (YYYY-MM-DD) : 2026-09-20
Expected return date (YYYY-MM-DD): 2026-09-22
Purpose of booking: Fest photography

========== BOOKING CONFIRMATION ==========
Booking ID   : BKG004
Member       : Rahul Nair (MEM003)
Equipment    : Camera Tripod (EQP004)
Club         : Photography Club
From         : 2026-09-20
Until        : 2026-09-22
Purpose      : Fest photography
Status       : REQUESTED
==========================================
The booking is waiting for coordinator approval (menu option 8).
```

## 6. Conflict detection — overlapping dates rejected

Second attempt to book the same tripod from 21 to 23 September, which overlaps BKG004:

```
Error: this equipment is already booked from 2026-09-20 to 2026-09-22 (booking BKG004).
```

## 7. Damaged equipment rejected

Attempt to book EQP005, the LED light panel that is marked DAMAGED:

```
Error: this equipment is DAMAGED and cannot be booked right now.
```

## 8. Approval, issue and return

```
----- Approve Booking -----

BKG ID   MEMBER   EQUIP    CLUB     FROM        DUE         RETURNED    STATUS
-------------------------------------------------------------------------------------
BKG004   MEM003   EQP004   CLB001   2026-09-20  2026-09-22  NONE        REQUESTED
Total bookings shown: 1
Enter booking ID to approve: BKG004
Booking BKG004 approved. It can now be issued (menu option 10).
```

```
Enter booking ID to issue: BKG004
Equipment Camera Tripod issued to Rahul Nair.
Please return it on or before 2026-09-22.
```

```
Enter booking ID being returned: BKG004
Actual return date (YYYY-MM-DD): 2026-09-22
Condition after return (1 = Good, 2 = Damaged, 3 = Needs maintenance): 1
Equipment returned in good condition and is available again.
Booking BKG004 is now RETURNED.
```

## 9. Maintenance (menu 12 → 1)

```
Enter equipment ID to send for maintenance: EQP003
Reason / notes: Lamp needs service
Epson Projector is now UNDER_MAINTENANCE.
```

## 10. Reports (menu 13)

```
==================================================
                CLUBGEAR REPORTS
            Generated on 2026-09-16
==================================================
Total clubs                : 2
Total registered members   : 3
Total equipment items      : 5
Available equipment        : 3
Currently booked equipment : 1
Damaged equipment          : 1
Equipment under maintenance: 0
Total bookings made        : 3
Completed (returned)       : 1
Cancelled bookings         : 0
Overdue bookings           : 1

--- Overdue bookings ---
BKG002 | Sneha Patil | Wireless Microphone | due 2026-09-15 | late by 1 day(s)

--- Most frequently booked equipment ---
EQP001 - Canon DSLR Camera : 1 booking(s)
EQP002 - Wireless Microphone : 1 booking(s)
EQP003 - Epson Projector : 1 booking(s)
Most booked item: Canon DSLR Camera (EQP001) with 1 booking(s).

--- Club-wise equipment usage ---
Photography Club (CLB001) : 1 booking(s), 3 item(s) owned
Coding Club (CLB002) : 2 booking(s), 2 item(s) owned

--- Equipment needing attention ---
EQP005 - LED Light Panel : DAMAGED
==================================================
```

## 11. Invalid input handled without crashing

```
Enter your choice (1-16): abc
That is not a valid number. Please try again.
Enter your choice (1-16): 99
Please enter a number between 1 and 16.
Enter your choice (1-16):
```

```
Club name              : Photography Club
Error: a club named 'Photography Club' already exists.
```

```
Student name : Aditya Verma
Email        : aditya.verma@college.edu
Error: this email is already registered.
```

```
Quantity (1-500): 0
Please enter a number between 1 and 500.
Quantity (1-500):
```

## 12. Saving and reloading

```
Data saved successfully inside the 'data' folder.
Clubs: 2, Members: 3, Equipment: 5, Bookings: 4, Maintenance records: 2
```

On the next start-up:

```
Saved data found. Loading it now...

Data loaded successfully.
Clubs: 2, Members: 3, Equipment: 5, Bookings: 4, Maintenance records: 2
```

When the `data` folder is missing and Load Data is chosen:

```
No saved data was found, so nothing was loaded.
(The current data in memory has not been changed.)
```

## 13. Files written to disk

```
data/clubs.csv
data/members.csv
data/equipment.csv
data/bookings.csv
data/maintenance.csv
```

Contents of `data/equipment.csv` after the session:

```
# equipmentId,name,category,quantity,condition,ownerClubId,status
EQP001,Canon DSLR Camera,Camera,2,Good,CLB001,AVAILABLE
EQP002,Wireless Microphone,Audio,4,Good,CLB002,BOOKED
EQP003,Epson Projector,Display,1,Good,CLB002,UNDER_MAINTENANCE
EQP004,Camera Tripod,Camera,3,Good,CLB001,AVAILABLE
EQP005,LED Light Panel,Lighting,2,Poor,CLB001,DAMAGED
```

Contents of `data/maintenance.csv`:

```
# recordId,equipmentId,recordDate,action,notes
MNT001,EQP005,2026-09-11,DAMAGE_REPORTED,Light panel flickers after the cultural event
MNT002,EQP003,2026-09-16,SENT_FOR_MAINTENANCE,Lamp needs service
```

## 14. Automated validation tests

Output of `java -cp out:out-tests clubgear.ValidationTests` (first and last sections shown):

```
=========================================================
        ClubGear - Automated Validation Tests
=========================================================

[Booking date overlap logic]
  PASS  Fully inside the existing range overlaps
  PASS  Starts before and ends inside overlaps
  PASS  Starts inside and ends after overlaps
  PASS  Completely surrounding range overlaps
  PASS  Same start and end date overlaps
  PASS  Range touching the first day overlaps
  PASS  Range touching the last day overlaps
  PASS  Range finishing one day earlier does NOT overlap
  PASS  Range starting one day later does NOT overlap
  PASS  Range in a different month does NOT overlap

[Booking status logic]
  PASS  REQUESTED booking blocks the equipment
  PASS  APPROVED booking blocks the equipment
  PASS  ACTIVE booking blocks the equipment
  PASS  OVERDUE booking blocks the equipment
  PASS  RETURNED booking does NOT block the equipment
  PASS  CANCELLED booking does NOT block the equipment
  PASS  BookingStatus enum has exactly six states

...

[File storage]
  PASS  Reading a missing file returns an empty list instead of crashing
  PASS  Writing a file reports success
  PASS  The same number of data lines is read back
  PASS  The header comment line is not returned as data
  PASS  The content of the first line is unchanged
  PASS  A club read from the file can be rebuilt as an object
  PASS  The temporary test file is deleted after the test

---------------------------------------------------------
Total tests : 55
Passed      : 55
Failed      : 0
---------------------------------------------------------
RESULT: ALL TESTS PASSED
```
