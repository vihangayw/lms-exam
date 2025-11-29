package lk.mc.core.util;

import lk.mc.core.constants.ApplicationConstants;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Random;

/**
 * This class contains all common Text based formatting and generations.
 *
 * @author vihangawicks
 * @since 11/12/21
 * MC-lms
 */
public class TsStringUtils {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Created a random passwords based on application default character limits
     *
     * @return generated password
     */
    public static String getRandomPassword() {
        return getRandomPassword(ApplicationConstants.PW_CHAR_LIMIT);
    }

    /**
     * Create a random password based on a given number of character limit
     *
     * @param length no of chaters that should be on the password
     * @return generated password
     */
    public static String getRandomPassword(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    /**
     * Provides a token friendly number style.
     *
     * @param number integer number that needs to format
     * @return formatted number as string
     */
    public static String formatNumber(int number) {
        if (number > 9999 || number < 0)
            return String.valueOf(number);
        if (number == 0) {
            return "0000";
        } else if (number <= 9) {
            return "000" + (number);
        } else if (number <= 99) {
            return "00" + (number);
        } else if (number <= 999) {
            return "0" + (number);
        } else {
            return String.valueOf(number);
        }
    }

    /**
     * Provides a token friendly number style.
     *
     * @param number integer number that needs to format
     * @return formatted number as string
     */
    public static String formatNumber2Digits(int number) {
        if (number < 10)
            return "0" + (number);

        return String.valueOf(number);

    }

    /**
     * Check a string is null or empty
     *
     * @param arg string argument
     * @return whether the string is null or empty
     */
    public static boolean isNullOrEmpty(String arg) {
        return arg == null || arg.isEmpty();
    }

    /**
     * generate random id with prefix
     *
     * @param prefix prefix id or null to set default (ts)
     * @return random id
     */
    public static String generateId(String prefix) {
        String id_1 = RandomStringUtils.randomAlphanumeric(8);
        String id_2 = RandomStringUtils.randomAlphanumeric(new Random().nextInt(5) + 1);
        if (prefix == null || StringUtils.isEmpty(prefix))
            prefix = RandomStringUtils.randomAlphanumeric(new Random().nextInt(4) + 1);
        return prefix.concat("-").concat(id_2).concat("-").concat(id_1);
    }

    public static String generateIdR(String prefix) {
        String id_1 = RandomStringUtils.randomAlphanumeric(5);
        if (prefix == null || StringUtils.isEmpty(prefix))
            prefix = RandomStringUtils.randomAlphanumeric(new Random().nextInt(4) + 1);
        return prefix.concat("-").concat(id_1);
    }

    public static String generateId(int prefix) {
        return generateId(String.valueOf(prefix));
    }

    /**
     * generate random id with prefix using current time in millis.
     * can be used to sort from id
     *
     * @param prefix prefix id or null to set default (ts)
     * @return random id
     */
    public static String generateWithMillisId(String prefix) {
        String id_1 = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String id_2 = RandomStringUtils.randomAlphanumeric(4);
        if (prefix == null || StringUtils.isEmpty(prefix))
            prefix = RandomStringUtils.randomAlphanumeric(new Random().nextInt(3) + 2);
        return prefix.concat("-").concat(id_1).concat("-").concat(id_2);
    }

    public static String generateWithMillisId(int prefix) {
        return generateWithMillisId(String.valueOf(prefix));
    }
}
