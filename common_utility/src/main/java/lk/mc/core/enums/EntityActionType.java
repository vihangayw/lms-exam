package lk.mc.core.enums;

/**
 * EntityActionType contains the actions which is performed to master data entries
 *
 * @author vihanga
 * @since 26/10/2021
 * MC-lms
 */
public enum EntityActionType {
    ADD(1), //Add
    EDIT(2), //Edit
    CHANGE_STATUS(3), //Change status
    UNKNOWN(-1);


    int id;

    EntityActionType(int id) {
        this.id = id;
    }

    public static EntityActionType getEnum(int id) {
        switch (id) {
            case 1:
                return ADD;
            case 2:
                return EDIT;
            case 3:
                return CHANGE_STATUS;
            default:
                return UNKNOWN;
        }
    }

    public int getId() {
        return this.id;
    }
}

