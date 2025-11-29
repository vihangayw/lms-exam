package lk.mc.core.enums;

/**
 * LoginStatusMaster contains different login statuses master data
 *
 * @author vihanga
 * @since 27/10/2021
 * MC-lms
 */
public enum LoginStatusMaster {
    PENDING(0),
    ACTIVE(1),
    LOCKED(2),
    SUSPENDED(3);

    private int code;

    LoginStatusMaster(int code) {
        this.code = code;
    }

    public static LoginStatusMaster getEnum(int code) {
        switch (code) {
            case 0:
                return PENDING;
            case 1:
                return ACTIVE;
            case 2:
                return LOCKED;
            case 3:
                return SUSPENDED;
            default:
                return PENDING;
        }
    }

    public int getCode() {
        return this.code;
    }
}
