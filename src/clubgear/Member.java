package clubgear;

/**
 * Represents a club member (a student who can book equipment).
 *
 * CSV format used by FileStorage (one member per line):
 * memberId,name,email,course,year,clubId
 */
public class Member {

    private String memberId;
    private String name;
    private String email;
    private String course;
    private int year;
    private String clubId;

    public Member(String memberId, String name, String email, String course, int year, String clubId) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.course = course;
        this.year = year;
        this.clubId = clubId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public int getYear() {
        return year;
    }

    public String getClubId() {
        return clubId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String toCsvLine() {
        return memberId + "," + name + "," + email + "," + course + "," + year + "," + clubId;
    }

    /** Rebuilds a Member from a CSV line. Returns null if the line is not valid. */
    public static Member fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 6) {
            return null;
        }
        try {
            int year = Integer.parseInt(parts[4]);
            return new Member(parts[0], parts[1], parts[2], parts[3], year, parts[5]);
        } catch (NumberFormatException e) {
            // The year column was not a number, so this line is skipped.
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%-8s %-20s %-28s %-8s %-5d %-8s",
                memberId, name, email, course, year, clubId);
    }
}
