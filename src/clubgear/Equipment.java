package clubgear;

/**
 * Represents one equipment record owned by a club,
 * for example "Canon DSLR Camera" owned by the Photography Club.
 *
 * NOTE (important simplification):
 * The quantity field tells us how many physical pieces the club owns,
 * but one equipment record is treated as ONE bookable item.
 * So two bookings for the same equipment ID cannot overlap in dates.
 * This keeps the booking logic simple and easy to understand.
 *
 * CSV format used by FileStorage (one equipment per line):
 * equipmentId,name,category,quantity,condition,ownerClubId,status
 */
public class Equipment {

    private String equipmentId;
    private String name;
    private String category;
    private int quantity;
    private String condition;          // Good / Average / Poor
    private String ownerClubId;
    private EquipmentStatus status;

    public Equipment(String equipmentId, String name, String category, int quantity,
                     String condition, String ownerClubId, EquipmentStatus status) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.condition = condition;
        this.ownerClubId = ownerClubId;
        this.status = status;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCondition() {
        return condition;
    }

    public String getOwnerClubId() {
        return ownerClubId;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /** Quantity must always stay greater than zero (business rule 3). */
    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    /**
     * Equipment can be booked only if it is not damaged and not under maintenance.
     * BOOKED is still allowed here because the equipment may be free on other dates;
     * the date overlap check is done separately in ClubGearSystem.
     */
    public boolean isBookable() {
        return status == EquipmentStatus.AVAILABLE || status == EquipmentStatus.BOOKED;
    }

    public String toCsvLine() {
        return equipmentId + "," + name + "," + category + "," + quantity + ","
                + condition + "," + ownerClubId + "," + status;
    }

    /** Rebuilds an Equipment object from a CSV line. Returns null if the line is broken. */
    public static Equipment fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 7) {
            return null;
        }
        try {
            int quantity = Integer.parseInt(parts[3]);
            EquipmentStatus status = EquipmentStatus.valueOf(parts[6]);
            return new Equipment(parts[0], parts[1], parts[2], quantity, parts[4], parts[5], status);
        } catch (IllegalArgumentException e) {
            // NumberFormatException is also an IllegalArgumentException,
            // so this single catch covers a bad quantity and a bad status word.
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%-8s %-24s %-14s %-4d %-9s %-8s %-18s",
                equipmentId, name, category, quantity, condition, ownerClubId, status);
    }
}
