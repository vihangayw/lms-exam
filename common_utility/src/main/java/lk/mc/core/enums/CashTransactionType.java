package lk.mc.core.enums;

/**
 * CashTransactionType contains the cash transaction types
 *
 * @author vihanga
 * @since 26/10/2021
 * MC-lms
 */
public enum CashTransactionType {
    DEPOSIT(1),
    WITHDRAW(2);

    int id;

    CashTransactionType(int id) {
        this.id = id;
    }

    public static CashTransactionType getEnum(int id) {
        if (1 == id) {
            return DEPOSIT;
        } else {
            return WITHDRAW;
        }
    }
}
