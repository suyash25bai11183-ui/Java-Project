package clubgear;

/**
 * Represents one college club (for example "Photography Club").
 *
 * This class only stores data about a club and gives access to it
 * through getters and setters (encapsulation: all fields are private).
 *
 * CSV format used by FileStorage (one club per line):
 * clubId,name,coordinatorName,category
 */
public class Club {

    private String clubId;
    private String name;
    private String coordinatorName;
    private String category;

    public Club(String clubId, String name, String coordinatorName, String category) {
        this.clubId = clubId;
        this.name = name;
        this.coordinatorName = coordinatorName;
        this.category = category;
    }

    public String getClubId() {
        return clubId;
    }

    public String getName() {
        return name;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public String getCategory() {
        return category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /** Converts this club into one CSV line so that it can be saved in a file. */
    public String toCsvLine() {
        return clubId + "," + name + "," + coordinatorName + "," + category;
    }

    /**
     * Builds a Club object back from a CSV line.
     * Returns null if the line is broken, so the caller can simply skip it.
     */
    public static Club fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 4) {
            return null;
        }
        return new Club(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return String.format("%-8s %-25s %-22s %-15s", clubId, name, coordinatorName, category);
    }
}
