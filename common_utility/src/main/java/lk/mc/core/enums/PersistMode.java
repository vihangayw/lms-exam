package lk.mc.core.enums;

/**
 * PersistMode contains the modes of JPA Persistence
 *
 * @author vihanga
 * @since 27/10/2021
 * MC-lms
 */
public enum PersistMode {
    JPA_PERSIST(1), //Use Direct Persistence API using JPA entity
    DB_SP_CALL(2); //Call database stored procedures

    int id;

    PersistMode(int id) {
        this.id = id;
    }

    public static PersistMode getEnum(int id) {
        if (1 == id) {
            return JPA_PERSIST;
        } else {
            return DB_SP_CALL;
        }
    }
}
