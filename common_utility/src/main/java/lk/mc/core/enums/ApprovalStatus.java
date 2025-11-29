package lk.mc.core.enums;

/**
 * ApprovalStatus contains available approval status list
 *
 * @author vihanga
 * @since 26/10/2021
 * MC-lms
 */
public enum ApprovalStatus {

    PENDING(1),
    APPROVED(2),
    REJECTED(3),
    MARKED_AS_DELETED(4),
    DELETED(5),
    APPROVED_L1(6),
    APPROVED_L2(7),
    SUSPENDED(8),
    UNKNOWN(-1);

    int id;

    ApprovalStatus(int id) {
        this.id = id;
    }

    public static ApprovalStatus getEnum(int id) {
        switch (id) {
            case 1:
                return PENDING;
            case 2:
                return APPROVED;
            case 3:
                return REJECTED;
            case 4:
                return MARKED_AS_DELETED;
            case 5:
                return DELETED;
            case 6:
                return APPROVED_L1;
            case 7:
                return APPROVED_L2;
            case 8:
                return SUSPENDED;
            default:
                return UNKNOWN;
        }
    }

    public int getId() {
        return this.id;
    }
}
