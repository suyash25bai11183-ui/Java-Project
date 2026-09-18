package clubgear;

/**
 * All the states a booking can be in during its life cycle.
 *
 * Normal flow:
 * REQUESTED -> APPROVED -> ACTIVE -> RETURNED
 * A booking can also be CANCELLED (before it is issued)
 * or become OVERDUE (issued but not returned on time).
 */
public enum BookingStatus {
    REQUESTED,
    APPROVED,
    ACTIVE,
    RETURNED,
    CANCELLED,
    OVERDUE
}
