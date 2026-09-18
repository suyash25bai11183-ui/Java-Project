package clubgear;

/**
 * The current physical state of a piece of equipment.
 *
 * AVAILABLE          - lying in the club room, free to be issued
 * BOOKED             - currently issued to a member
 * DAMAGED            - broken, must not be given to anyone
 * UNDER_MAINTENANCE  - sent for repair / servicing
 */
public enum EquipmentStatus {
    AVAILABLE,
    BOOKED,
    DAMAGED,
    UNDER_MAINTENANCE
}
