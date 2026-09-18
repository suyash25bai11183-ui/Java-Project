package clubgear;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * One line in the maintenance history of a piece of equipment.
 *
 * The "action" word tells what happened, for example:
 * DAMAGE_REPORTED, SENT_FOR_MAINTENANCE, MAINTENANCE_COMPLETED
 *
 * CSV format used by FileStorage (one record per line):
 * recordId,equipmentId,recordDate,action,notes
 */
public class MaintenanceRecord {

    private String recordId;
    private String equipmentId;
    private LocalDate recordDate;
    private String action;
    private String notes;

    public MaintenanceRecord(String recordId, String equipmentId, LocalDate recordDate,
                             String action, String notes) {
        this.recordId = recordId;
        this.equipmentId = equipmentId;
        this.recordDate = recordDate;
        this.action = action;
        this.notes = notes;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public String getAction() {
        return action;
    }

    public String getNotes() {
        return notes;
    }

    public String toCsvLine() {
        return recordId + "," + equipmentId + "," + recordDate + "," + action + "," + notes;
    }

    /** Rebuilds a MaintenanceRecord from a CSV line. Returns null if the line is broken. */
    public static MaintenanceRecord fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 5) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(parts[2]);
            return new MaintenanceRecord(parts[0], parts[1], date, parts[3], parts[4]);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%-8s %-8s %-11s %-22s %s",
                recordId, equipmentId, recordDate, action, notes);
    }
}
