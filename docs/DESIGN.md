# ClubGear — Design Document

This document contains the architecture, workflow and UML design of the ClubGear system,
together with the storage design and the reasoning behind the main design decisions.

All diagrams are written in Mermaid, which GitHub renders automatically when this file is
viewed in the repository. The same diagrams are also available as SVG images in
`docs/diagrams/` (architecture, workflow, usecase, class, sequence, er) and are embedded in
`ClubGear_Project_Report.pdf`.

---

## 1. System Architecture

ClubGear follows a simple three-layer architecture. Each layer talks only to the layer
directly below it, which keeps the code easy to follow and easy to change.

```mermaid
graph TD
    U([User: coordinator or club member])

    subgraph P["Presentation Layer"]
        M["Main.java<br/>program entry, banner, Scanner"]
        CS["ClubGearSystem.java<br/>menu loop and one method per operation"]
        IV["InputValidator.java<br/>safe keyboard input and validation"]
    end

    subgraph L["Domain / Business Logic Layer"]
        CL["Club"]
        ME["Member"]
        EQ["Equipment"]
        BK["Booking<br/>holds the date overlap rule"]
        MR["MaintenanceRecord"]
        BS["BookingStatus (enum)"]
        ES["EquipmentStatus (enum)"]
    end

    subgraph S["Persistence Layer"]
        FS["FileStorage.java<br/>CSV read and write"]
        DF[("data/*.csv<br/>five text files")]
    end

    U -->|types menu choices| M
    M --> CS
    CS <--> IV
    CS --> CL
    CS --> ME
    CS --> EQ
    CS --> BK
    CS --> MR
    BK --- BS
    EQ --- ES
    CS <--> FS
    FS <--> DF
```

**Why the layers are separated**

| Layer | Responsibility | Classes |
|-------|----------------|---------|
| Presentation | Show the menu, read input, print results and error messages | `Main`, `ClubGearSystem`, `InputValidator` |
| Domain | Hold the data and the rules that belong to the data itself | `Club`, `Member`, `Equipment`, `Booking`, `MaintenanceRecord`, the two enums |
| Persistence | Turn objects into text lines and text lines back into objects | `FileStorage`, plus the `toCsvLine()` / `fromCsvLine()` methods |

The domain classes never print anything and never read the keyboard, so they can be tested
directly by `tests/clubgear/ValidationTests.java` without any user interaction.

---

## 2. Process Flow / Workflow Diagram

This is the complete life cycle of one booking, which is the central workflow of the system.

```mermaid
flowchart TD
    A([Start]) --> B[Member chooses<br/>option 6: Create Booking]
    B --> C{Member ID<br/>exists?}
    C -- No --> X[Show error<br/>return to menu]
    C -- Yes --> D{Equipment ID<br/>exists?}
    D -- No --> X
    D -- Yes --> E{Equipment DAMAGED or<br/>UNDER_MAINTENANCE?}
    E -- Yes --> X
    E -- No --> F{Start date<br/>in the past?}
    F -- Yes --> X
    F -- No --> G{Return date<br/>before start date?}
    G -- Yes --> X
    G -- No --> H{Dates overlap an<br/>existing live booking?}
    H -- Yes --> X
    H -- No --> I[Create booking<br/>status = REQUESTED<br/>print confirmation]

    I --> J{Coordinator decision}
    J -- Option 9: Cancel --> K[status = CANCELLED<br/>equipment released]
    J -- Option 8: Approve --> L[status = APPROVED]
    L --> M{Coordinator action}
    M -- Option 9: Cancel --> K
    M -- Option 10: Issue --> N[status = ACTIVE<br/>equipment = BOOKED]
    N --> O{Due date passed<br/>before return?}
    O -- Yes --> P[status = OVERDUE<br/>detected automatically]
    O -- No --> Q[Option 11: Return]
    P --> Q
    Q --> R{Condition after return}
    R -- Good --> S[status = RETURNED<br/>equipment = AVAILABLE]
    R -- Damaged --> T[status = RETURNED<br/>equipment = DAMAGED<br/>maintenance record added]
    R -- Needs service --> V[status = RETURNED<br/>equipment = UNDER_MAINTENANCE<br/>maintenance record added]
    T --> W[Option 12: repair<br/>equipment = AVAILABLE]
    V --> W
    K --> Z([End])
    S --> Z
    W --> Z
    X --> Z
```

---

## 3. Use Case Diagram

```mermaid
graph LR
    COORD([Club Coordinator])
    MEMBER([Club Member])
    INCHARGE([Equipment In-charge])

    subgraph ClubGear["ClubGear System"]
        UC1["Add club"]
        UC2["Register member"]
        UC3["Add equipment"]
        UC4["View / search records"]
        UC5["Create booking"]
        UC6["Approve booking"]
        UC7["Cancel booking"]
        UC8["Issue equipment"]
        UC9["Return equipment"]
        UC10["Record damage"]
        UC11["Manage maintenance"]
        UC12["View reports"]
        UC13["Save / load data"]
    end

    MEMBER --> UC4
    MEMBER --> UC5
    MEMBER --> UC9

    COORD --> UC1
    COORD --> UC2
    COORD --> UC3
    COORD --> UC4
    COORD --> UC6
    COORD --> UC7
    COORD --> UC8
    COORD --> UC12
    COORD --> UC13

    INCHARGE --> UC9
    INCHARGE --> UC10
    INCHARGE --> UC11
```

| Use case | Actor | Pre-condition | Post-condition |
|---|---|---|---|
| Create booking | Member | Member, equipment and club exist; equipment is not damaged | A booking with status REQUESTED exists |
| Approve booking | Coordinator | Booking status is REQUESTED | Booking status becomes APPROVED |
| Issue equipment | Coordinator | Booking status is APPROVED | Booking becomes ACTIVE, equipment becomes BOOKED |
| Return equipment | Member / In-charge | Booking is ACTIVE or OVERDUE | Booking becomes RETURNED, equipment released or flagged |
| Manage maintenance | In-charge | Equipment is DAMAGED or UNDER_MAINTENANCE | Equipment becomes AVAILABLE, history entry written |
| View reports | Coordinator | None | Statistics printed on the screen |

---

## 4. Class Diagram

```mermaid
classDiagram
    class Main {
        +main(String[] args)$ void
    }

    class ClubGearSystem {
        -ArrayList~Club~ clubs
        -ArrayList~Member~ members
        -ArrayList~Equipment~ equipmentList
        -ArrayList~Booking~ bookings
        -ArrayList~MaintenanceRecord~ maintenanceRecords
        -HashSet~String~ registeredEmails
        -int clubCounter
        -int bookingCounter
        -InputValidator input
        -FileStorage storage
        +startUp() void
        +start() void
        -addClub() void
        -registerMember() void
        -addEquipment() void
        -createBooking() void
        -approveBooking() void
        -cancelBooking() void
        -issueEquipment() void
        -returnEquipment() void
        -manageMaintenance() void
        -viewReports() void
        -saveData() void
        -loadData() void
        -findClashingBooking(String, LocalDate, LocalDate) Booking
        -updateOverdueBookings() void
    }

    class Club {
        -String clubId
        -String name
        -String coordinatorName
        -String category
        +getClubId() String
        +getName() String
        +toCsvLine() String
        +fromCsvLine(String)$ Club
    }

    class Member {
        -String memberId
        -String name
        -String email
        -String course
        -int year
        -String clubId
        +getEmail() String
        +toCsvLine() String
        +fromCsvLine(String)$ Member
    }

    class Equipment {
        -String equipmentId
        -String name
        -String category
        -int quantity
        -String condition
        -String ownerClubId
        -EquipmentStatus status
        +setQuantity(int) void
        +isBookable() boolean
        +toCsvLine() String
        +fromCsvLine(String)$ Equipment
    }

    class Booking {
        -String bookingId
        -String memberId
        -String equipmentId
        -String clubId
        -LocalDate startDate
        -LocalDate expectedReturnDate
        -LocalDate actualReturnDate
        -String purpose
        -BookingStatus status
        -LocalDateTime createdAt
        -String returnCondition
        -String damageNotes
        +isBlockingEquipment() boolean
        +overlapsWith(LocalDate, LocalDate) boolean
        +toCsvLine() String
        +fromCsvLine(String)$ Booking
    }

    class MaintenanceRecord {
        -String recordId
        -String equipmentId
        -LocalDate recordDate
        -String action
        -String notes
        +toCsvLine() String
        +fromCsvLine(String)$ MaintenanceRecord
    }

    class FileStorage {
        +String DATA_FOLDER$
        +writeLines(String, String, List~String~) boolean
        +readLines(String) ArrayList~String~
        +savedDataExists() boolean
    }

    class InputValidator {
        -Scanner scanner
        +readInt(String, int, int) int
        +readNonEmptyText(String) String
        +readId(String) String
        +readEmail(String) String
        +readDate(String) LocalDate
        +readYesNo(String) boolean
        +cleanText(String)$ String
        +isValidEmail(String)$ boolean
    }

    class BookingStatus {
        <<enumeration>>
        REQUESTED
        APPROVED
        ACTIVE
        RETURNED
        CANCELLED
        OVERDUE
    }

    class EquipmentStatus {
        <<enumeration>>
        AVAILABLE
        BOOKED
        DAMAGED
        UNDER_MAINTENANCE
    }

    Main --> ClubGearSystem : creates
    ClubGearSystem "1" *-- "0..*" Club
    ClubGearSystem "1" *-- "0..*" Member
    ClubGearSystem "1" *-- "0..*" Equipment
    ClubGearSystem "1" *-- "0..*" Booking
    ClubGearSystem "1" *-- "0..*" MaintenanceRecord
    ClubGearSystem --> InputValidator : uses
    ClubGearSystem --> FileStorage : uses
    Booking --> BookingStatus : has
    Equipment --> EquipmentStatus : has
    Member ..> Club : belongs to (clubId)
    Equipment ..> Club : owned by (ownerClubId)
    Booking ..> Member : made by (memberId)
    Booking ..> Equipment : reserves (equipmentId)
    MaintenanceRecord ..> Equipment : describes (equipmentId)
```

**Note on the relationships.** `Member`, `Equipment`, `Booking` and `MaintenanceRecord` do
not hold object references to each other. They store the ID of the related record
(`clubId`, `equipmentId`, `memberId`) and the lookup happens through the helper methods
`findClubById()`, `findMemberById()` and `findEquipmentById()` inside `ClubGearSystem`.
This mirrors how foreign keys work in a database and makes CSV saving straightforward,
because each line stores only plain text.

---

## 5. Sequence Diagram — Creating a Booking

This is the most rule-heavy operation in the system, so it is the one worth showing in detail.

```mermaid
sequenceDiagram
    actor User as Club Member
    participant CS as ClubGearSystem
    participant IV as InputValidator
    participant EQ as Equipment
    participant BK as Booking (existing)
    participant NEW as Booking (new)

    User->>CS: chooses menu option 6
    CS->>CS: printMembers(), printEquipment()
    CS->>IV: readId("Enter member ID")
    IV-->>CS: "MEM003"
    CS->>CS: findMemberById("MEM003")

    CS->>IV: readId("Enter equipment ID")
    IV-->>CS: "EQP004"
    CS->>CS: findEquipmentById("EQP004")
    CS->>EQ: isBookable()
    EQ-->>CS: true

    CS->>IV: readDate("Booking start date")
    IV-->>CS: 2026-09-20
    CS->>IV: readDate("Expected return date")
    IV-->>CS: 2026-09-22
    CS->>CS: check returnDate >= startDate

    CS->>CS: findClashingBooking("EQP004", 09-20, 09-22)
    loop for every existing booking of EQP004
        CS->>BK: isBlockingEquipment()
        BK-->>CS: true / false
        CS->>BK: overlapsWith(09-20, 09-22)
        BK-->>CS: true / false
    end
    CS-->>CS: no clash found

    CS->>IV: readNonEmptyText("Purpose of booking")
    IV-->>CS: "Fest photography"
    CS->>NEW: new Booking(BKG004, ..., REQUESTED)
    NEW-->>CS: booking object
    CS->>CS: bookings.add(booking)
    CS-->>User: BOOKING CONFIRMATION (BKG004, status REQUESTED)
```

**The alternative flow** (what happens when a clash is found): `findClashingBooking()`
returns the clashing booking instead of `null`, the system prints
`Error: this equipment is already booked from ... (booking BKG004).`, no `Booking` object
is created, and control returns to the main menu.

---

## 6. Storage Design

ClubGear does not use a database. It stores five CSV text files inside a `data` folder.
The design is still relational in spirit: each file behaves like a table, the first column
is the primary key, and the ID columns act as foreign keys.

### 6.1 ER Diagram

```mermaid
erDiagram
    CLUB ||--o{ MEMBER : "has members"
    CLUB ||--o{ EQUIPMENT : "owns"
    CLUB ||--o{ BOOKING : "is billed for"
    MEMBER ||--o{ BOOKING : "makes"
    EQUIPMENT ||--o{ BOOKING : "is reserved in"
    EQUIPMENT ||--o{ MAINTENANCE_RECORD : "has history"

    CLUB {
        string clubId PK
        string name UK
        string coordinatorName
        string category
    }
    MEMBER {
        string memberId PK
        string name
        string email UK
        string course
        int year
        string clubId FK
    }
    EQUIPMENT {
        string equipmentId PK
        string name
        string category
        int quantity
        string condition
        string ownerClubId FK
        enum status
    }
    BOOKING {
        string bookingId PK
        string memberId FK
        string equipmentId FK
        string clubId FK
        date startDate
        date expectedReturnDate
        date actualReturnDate
        string purpose
        enum status
        datetime createdAt
        string returnCondition
        string damageNotes
    }
    MAINTENANCE_RECORD {
        string recordId PK
        string equipmentId FK
        date recordDate
        string action
        string notes
    }
```

### 6.2 File Schema

Every file begins with one header line starting with `#`, which the loader skips.
Values are separated by commas, and commas typed by the user are replaced with spaces by
`InputValidator.cleanText()` before the value is ever stored.

**data/clubs.csv**

| Column | Type | Rule |
|---|---|---|
| clubId | text | Primary key, format `CLB001` |
| name | text | Unique, not empty |
| coordinatorName | text | Not empty |
| category | text | Not empty |

**data/members.csv**

| Column | Type | Rule |
|---|---|---|
| memberId | text | Primary key, format `MEM001` |
| name | text | Not empty |
| email | text | Unique, must contain one `@` and a dot in the domain |
| course | text | Not empty |
| year | integer | 1 to 4 |
| clubId | text | Foreign key to clubs.csv |

**data/equipment.csv**

| Column | Type | Rule |
|---|---|---|
| equipmentId | text | Primary key, format `EQP001` |
| name | text | Not empty |
| category | text | Not empty |
| quantity | integer | Greater than zero |
| condition | text | Good / Average / Poor |
| ownerClubId | text | Foreign key to clubs.csv |
| status | enum | AVAILABLE / BOOKED / DAMAGED / UNDER_MAINTENANCE |

**data/bookings.csv**

| Column | Type | Rule |
|---|---|---|
| bookingId | text | Primary key, format `BKG001` |
| memberId | text | Foreign key to members.csv |
| equipmentId | text | Foreign key to equipment.csv |
| clubId | text | Foreign key to clubs.csv |
| startDate | date | ISO format `YYYY-MM-DD` |
| expectedReturnDate | date | Not earlier than startDate |
| actualReturnDate | date | `NONE` until the item is returned |
| purpose | text | Not empty, no commas |
| status | enum | REQUESTED / APPROVED / ACTIVE / RETURNED / CANCELLED / OVERDUE |
| createdAt | datetime | ISO `LocalDateTime` |
| returnCondition | text | `NONE` until returned |
| damageNotes | text | `NONE` when nothing is damaged |

**data/maintenance.csv**

| Column | Type | Rule |
|---|---|---|
| recordId | text | Primary key, format `MNT001` |
| equipmentId | text | Foreign key to equipment.csv |
| recordDate | date | ISO format |
| action | text | DAMAGE_REPORTED / SENT_FOR_MAINTENANCE / MAINTENANCE_COMPLETED |
| notes | text | Free text without commas |

### 6.3 Real saved data

This is the actual content of `data/bookings.csv` after the demo session:

```
# bookingId,memberId,equipmentId,clubId,startDate,expectedReturnDate,actualReturnDate,purpose,status,createdAt,returnCondition,damageNotes
BKG001,MEM001,EQP001,CLB001,2026-09-17,2026-09-19,NONE,Annual day photo coverage,APPROVED,2026-09-16T07:03:37.992917178,NONE,NONE
BKG002,MEM002,EQP002,CLB002,2026-09-10,2026-09-15,NONE,Tech talk audio setup,OVERDUE,2026-09-16T07:03:37.992948339,NONE,NONE
BKG003,MEM003,EQP003,CLB002,2026-08-27,2026-08-29,2026-08-29,Seminar presentation,RETURNED,2026-09-16T07:03:37.992995027,Good,NONE
BKG004,MEM003,EQP004,CLB001,2026-09-20,2026-09-22,2026-09-22,Fest photography,RETURNED,2026-09-16T07:03:38.101090557,Good,NONE
```

---

## 7. Design Decisions and Rationale

### 7.1 Why no inheritance or interfaces

An obvious temptation is a `Person` superclass for `Member`, or an `Entity` interface with
`toCsvLine()`. Both were rejected:

- `Club`, `Member`, `Equipment` and `Booking` share no behaviour, only the coincidence that
  each has an ID. Inheritance built on a coincidence produces a base class that has to be
  defended in a viva and cannot be.
- `fromCsvLine()` has to be **static** (it creates the object), and static methods cannot be
  declared in an interface as an instance contract, so an interface would only cover half the
  conversion and split the logic in two.

The decision is to use plain classes, and to be able to explain why.

### 7.2 Why an equipment record is one bookable asset

The alternative was a live stock count: quantity 4 means four simultaneous bookings.
That requires tracking how many units are out on every overlapping date range, which is a
genuinely harder algorithm than the syllabus needs. The chosen rule — one record, one
bookable asset, two cameras added as two records — keeps `findClashingBooking()` to eight
readable lines while still enforcing business rules 5 and 6 correctly.

### 7.3 Why the overlap test is written as a negation

```java
boolean noOverlap = otherEnd.isBefore(startDate) || otherStart.isAfter(expectedReturnDate);
return !noOverlap;
```

Testing for overlap directly needs four separate cases (inside, straddling the start,
straddling the end, surrounding). Testing for **non**-overlap needs only two, because two
ranges fail to touch only when one ends before the other begins. The negation is shorter and
far easier to prove correct — and `ValidationTests` checks all ten boundary cases, including
the day-exactly-touching ones.

### 7.4 Why OVERDUE is allowed to be returned

Business rule 9 says only active bookings can be returned. OVERDUE is not a different kind of
booking; it is an ACTIVE booking that is late. Refusing to accept a late return would leave
the equipment permanently locked, so `returnEquipment()` accepts both ACTIVE and OVERDUE and
prints a note that the item came back late.

### 7.5 Why commas are stripped from user input

The files use commas as separators, so a club named `Photography, Film & Media` would break
the line into five columns instead of four and silently corrupt the file on the next load.
Rather than writing a quoting and escaping parser (which is real work and not the point of
the exercise), `InputValidator.cleanText()` replaces commas with spaces at the moment of
entry. The limitation is documented rather than hidden.

### 7.6 Why every input goes through `readLine()`

Mixing `nextInt()` and `nextLine()` on one `Scanner` is the classic Java beginner bug: the
newline left behind by `nextInt()` is swallowed by the next `nextLine()`, which then returns
an empty string. `InputValidator` reads **every** input as a full line and parses it
afterwards, so the bug cannot occur anywhere in the program.

### 7.7 Why status changes are checked before they are applied

Each of `approveBooking()`, `cancelBooking()`, `issueEquipment()` and `returnEquipment()`
begins by testing the current status and refusing with a message naming the actual state
(`Error: booking BKG005 is CANCELLED. Only APPROVED bookings can be issued.`). This makes the
state machine in section 2 enforceable in one place per transition instead of being implied
across the code.
