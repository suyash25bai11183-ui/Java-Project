# Project Statement

**Project title:** ClubGear: A Campus Club Equipment Booking and Management System
**Type:** Second year B.Tech (Computer Science) Java mini project
**Nature:** Console based, single user, file storage

---

## 1. Problem Statement

College clubs own shared equipment such as cameras, microphones, speakers, projectors,
tripods, lighting equipment, extension boards and decoration material. This equipment is
borrowed by club members for events, workshops, seminars and cultural programmes.

At present most clubs manage this process manually, usually through a register kept in
the club room or through informal messages. This manual method creates several problems:

- Two members request the same item for the same date and the clash is discovered only
  on the day of the event.
- There is no reliable record of who is currently holding an item.
- Items are returned late and nobody notices until the item is needed again.
- Damaged equipment is issued again because the damage was never written down.
- Coordinators cannot answer simple questions such as "which item is used most often" or
  "how many items are out of order right now".

A small software system is needed that records equipment, records bookings, applies
booking rules automatically, and keeps the data even after the program is closed.

---

## 2. Objectives

1. To maintain a digital record of clubs, members and club owned equipment.
2. To allow members to book equipment for a chosen date range with a stated purpose.
3. To prevent double bookings by checking date overlaps automatically.
4. To prevent the booking of damaged or under maintenance equipment.
5. To manage a booking through its complete life cycle: requested, approved, active,
   returned, cancelled and overdue.
6. To record the condition of equipment after it is returned and to keep a maintenance
   history.
7. To detect overdue bookings using the current system date.
8. To generate simple reports that help a club coordinator take decisions.
9. To store all data in plain text files so that the information survives between runs.
10. To demonstrate the practical use of core Java concepts: classes, objects,
    encapsulation, constructors, collections, enums, exception handling, date classes and
    file handling.

---

## 3. Scope

**In scope**

- A menu driven console application written in Java 17.
- Management of clubs, members, equipment, bookings and maintenance records.
- Business rule checking for duplicates, quantities, dates, overlaps and status changes.
- Report generation on the screen.
- Saving and loading data using CSV text files.
- Sample data so that the system can be demonstrated immediately.

**Out of scope**

- Web or mobile interfaces.
- Databases such as MySQL, and frameworks such as Spring Boot or Hibernate.
- User authentication, passwords and access control.
- Payment, fine collection or accounting features.
- Networking, multithreading and concurrent access by several users.
- Any external library or build tool.

---

## 4. Functional Requirements

| No. | Requirement |
|-----|-------------|
| FR1 | The system shall add a club with a unique auto generated ID and reject duplicate club names. |
| FR2 | The system shall display all clubs and search clubs by name or ID. |
| FR3 | The system shall register a member with a unique auto generated ID and reject a duplicate email. |
| FR4 | The system shall display all members and search members by name or ID. |
| FR5 | The system shall add equipment with a unique auto generated ID and accept only a quantity greater than zero. |
| FR6 | The system shall display equipment, search it by name or category, and update its condition. |
| FR7 | The system shall mark equipment as AVAILABLE, BOOKED, DAMAGED or UNDER_MAINTENANCE. |
| FR8 | The system shall create a booking containing member, equipment, club, start date, expected return date and purpose. |
| FR9 | The system shall reject a booking whose return date is earlier than its start date. |
| FR10 | The system shall reject a booking that overlaps an existing live booking of the same equipment. |
| FR11 | The system shall reject a booking of damaged or under maintenance equipment. |
| FR12 | The system shall display a booking confirmation after a successful booking. |
| FR13 | The system shall allow a requested booking to be approved or cancelled. |
| FR14 | The system shall allow only an approved booking to be issued and become active. |
| FR15 | The system shall allow only an issued booking (active or overdue) to be returned. |
| FR16 | The system shall record the actual return date, the condition after return and damage notes. |
| FR17 | The system shall release the equipment when a booking is returned or cancelled. |
| FR18 | The system shall detect overdue bookings by comparing the due date with the current date. |
| FR19 | The system shall allow equipment to be sent for maintenance and later marked as repaired. |
| FR20 | The system shall keep a maintenance history for every item. |
| FR21 | The system shall display reports covering totals, availability, overdue bookings, most booked equipment and club-wise usage. |
| FR22 | The system shall save all data to CSV files and load it back on request or at start up. |
| FR23 | The system shall continue running normally when an invalid menu choice, an invalid number or an invalid date is entered. |

---

## 5. Non-Functional Requirements

| No. | Requirement |
|-----|-------------|
| NFR1 | **Usability.** The menu shall be numbered, and every error message shall state clearly what went wrong. |
| NFR2 | **Reliability.** No invalid input shall terminate the program unexpectedly. |
| NFR3 | **Portability.** The program shall run on Windows, Linux and macOS with only a JDK 17 installation. |
| NFR4 | **Maintainability.** Each class shall have a single responsibility, and methods shall stay short and commented. |
| NFR5 | **Performance.** All searches operate on in-memory lists, so every operation responds instantly for a few hundred records. |
| NFR6 | **Data safety.** A missing or partly damaged data file shall be skipped with a message instead of crashing the program. |
| NFR7 | **Simplicity.** The code shall use only core Java features taught in the second year syllabus. |

---

## 6. Target Users

| User | How they use ClubGear |
|------|----------------------|
| **Club coordinator** (mainly a faculty member or senior student) | Adds the club, adds equipment, approves and cancels bookings, issues and receives equipment, manages maintenance and reads the reports. |
| **Club member** (student) | Checks which equipment is free, requests a booking with a purpose, and returns the equipment on time. |
| **Equipment in-charge / volunteer** | Records the condition of returned items, reports damage and updates the maintenance status. |
| **Department or college office** | Uses the reports to see how much equipment each club owns and how often it is used. |

---

## 7. High-Level Features

The system is built from seven functional modules. The course guidelines ask for a minimum
of three; ClubGear has seven because the booking life cycle only makes sense when equipment,
members and maintenance are all present.

| # | Module | What it does | Main classes |
|---|--------|--------------|--------------|
| 1 | **Club management** | Create clubs with unique IDs, view and search them, block duplicate names | `Club`, `ClubGearSystem` |
| 2 | **Member management** | Register members with unique IDs, validate and de-duplicate emails, view and search | `Member`, `InputValidator` |
| 3 | **Equipment management** | Add equipment with unique IDs, track quantity, condition, owning club and status | `Equipment`, `EquipmentStatus` |
| 4 | **Equipment booking** | Create bookings, check availability, detect date clashes, print a confirmation | `Booking`, `ClubGearSystem` |
| 5 | **Booking status management** | Approve, cancel, issue and return; detect overdue bookings; block invalid transitions | `BookingStatus`, `Booking` |
| 6 | **Return and maintenance** | Record return date and condition, log damage, send for repair, mark repaired | `MaintenanceRecord`, `Equipment` |
| 7 | **Reports** | Totals, availability, overdue list, most booked item, club-wise usage | `ClubGearSystem` (HashMap based counting) |

Two supporting modules run underneath all seven:

| Module | What it does | Class |
|--------|--------------|-------|
| **Input validation** | Every keyboard read is range checked, format checked and re-prompted on error | `InputValidator` |
| **Persistence** | Objects are converted to CSV lines and back; missing or damaged files are survived | `FileStorage` plus `toCsvLine()` / `fromCsvLine()` in each model |

**Input and output structure**

- *Input:* numbered menu choices, IDs typed as text (`MEM003`), dates typed as `YYYY-MM-DD`,
  free text for names, purposes and notes, and `y`/`n` confirmations.
- *Output:* formatted tables for lists, a boxed confirmation slip for a new booking, a reports
  page, specific error messages naming the exact rule that was broken, and five CSV files
  on disk.

---

## 8. Expected Outcome

At the end of the project, a working Java console application is available that:

- runs from the command line using only a JDK,
- stores clubs, members, equipment, bookings and maintenance records,
- applies twelve booking and validation rules automatically,
- shows a clear confirmation for every successful action and a clear reason for every
  refused action,
- detects overdue bookings against the current date,
- produces an on-screen report for the club coordinator,
- keeps its data in readable CSV files between runs,
- and demonstrates practical use of classes, objects, encapsulation, collections, enums,
  exception handling, date handling and file handling as required by the second year
  Java syllabus.
