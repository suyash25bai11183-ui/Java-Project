package clubgear;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Represents one booking: a member takes one equipment item
 * from a club for a period of dates.
 *
 * CSV format used by FileStorage (one booking per line):
 * bookingId,memberId,equipmentId,clubId,startDate,expectedReturnDate,
 * actualReturnDate,purpose,status,createdAt,returnCondition,damageNotes
 *
 * Fields that are still empty are written as the word NONE.
 */
public class Booking {

    private String bookingId;
    private String memberId;
    private String equipmentId;
    private String clubId;
    private LocalDate startDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;   // null until the item comes back
    private String purpose;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private String returnCondition;       // null until the item comes back
    private String damageNotes;           // null if nothing was damaged

    public Booking(String bookingId, String memberId, String equipmentId, String clubId,
                   LocalDate startDate, LocalDate expectedReturnDate, String purpose,
                   BookingStatus status, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.memberId = memberId;
        this.equipmentId = equipmentId;
        this.clubId = clubId;
        this.startDate = startDate;
        this.expectedReturnDate = expectedReturnDate;
        this.purpose = purpose;
        this.status = status;
        this.createdAt = createdAt;
        this.actualReturnDate = null;
        this.returnCondition = null;
        this.damageNotes = null;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getClubId() {
        return clubId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getReturnCondition() {
        return returnCondition;
    }

    public String getDamageNotes() {
        return damageNotes;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public void setReturnCondition(String returnCondition) {
        this.returnCondition = returnCondition;
    }

    public void setDamageNotes(String damageNotes) {
        this.damageNotes = damageNotes;
    }

    /**
     * A booking is "blocking" the equipment if it is not finished yet.
     * Only blocking bookings are checked while looking for date clashes.
     */
    public boolean isBlockingEquipment() {
        return status == BookingStatus.REQUESTED
                || status == BookingStatus.APPROVED
                || status == BookingStatus.ACTIVE
                || status == BookingStatus.OVERDUE;
    }

    /**
     * Checks whether this booking clashes with the given date range.
     * Two ranges do NOT overlap only when one finishes before the other starts.
     */
    public boolean overlapsWith(LocalDate otherStart, LocalDate otherEnd) {
        boolean noOverlap = otherEnd.isBefore(startDate) || otherStart.isAfter(expectedReturnDate);
        return !noOverlap;
    }

    private static String orNone(Object value) {
        return (value == null) ? "NONE" : value.toString();
    }

    public String toCsvLine() {
        return bookingId + "," + memberId + "," + equipmentId + "," + clubId + ","
                + startDate + "," + expectedReturnDate + "," + orNone(actualReturnDate) + ","
                + purpose + "," + status + "," + createdAt + ","
                + orNone(returnCondition) + "," + orNone(damageNotes);
    }

    /** Rebuilds a Booking from a CSV line. Returns null if the line is broken. */
    public static Booking fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 12) {
            return null;
        }
        try {
            LocalDate start = LocalDate.parse(parts[4]);
            LocalDate expected = LocalDate.parse(parts[5]);
            BookingStatus status = BookingStatus.valueOf(parts[8]);
            LocalDateTime createdAt = LocalDateTime.parse(parts[9]);

            Booking booking = new Booking(parts[0], parts[1], parts[2], parts[3],
                    start, expected, parts[7], status, createdAt);

            if (!parts[6].equals("NONE")) {
                booking.setActualReturnDate(LocalDate.parse(parts[6]));
            }
            if (!parts[10].equals("NONE")) {
                booking.setReturnCondition(parts[10]);
            }
            if (!parts[11].equals("NONE")) {
                booking.setDamageNotes(parts[11]);
            }
            return booking;
        } catch (DateTimeParseException | IllegalArgumentException e) {
            // A date or the status word was not in the expected format.
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%-8s %-8s %-8s %-8s %-11s %-11s %-11s %-10s",
                bookingId, memberId, equipmentId, clubId,
                startDate, expectedReturnDate, orNone(actualReturnDate), status);
    }
}
