package lk.mc.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all common utilities.
 *
 * @author vihangawicks
 * @since 11/1/22
 * MC-lms
 */
public class ApplicationUtils {

    public static final Map<String, String> CONFIGS = new HashMap<>();
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static Logger logger = LogManager.getLogger(ApplicationUtils.class);

    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    public static boolean isTimeWithinRange(Date min, Date max) {
        Calendar minCalendar = Calendar.getInstance();
        minCalendar.setTime(min);
        minCalendar.set(2000, Calendar.JANUARY, 1);

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.setTime(max);
        maxCalendar.set(2000, Calendar.JANUARY, 1);

        Calendar now = Calendar.getInstance();
        now.set(2000, Calendar.JANUARY, 1);

        logger.info("Current Time | " + now.getTimeInMillis());
        logger.info("Open Time | " + minCalendar.getTimeInMillis());
        logger.info("Close Time | " + maxCalendar.getTimeInMillis());

        return now.getTimeInMillis() >= minCalendar.getTimeInMillis()
                && now.getTimeInMillis() <= maxCalendar.getTimeInMillis();
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    /**
     * This class defines the days of weeks in a integer format according to the {@link Calendar}
     */
    public class DaysOfWeek {
        public static final int SUNDAY = Calendar.SUNDAY;//1
        public static final int MONDAY = Calendar.MONDAY;
        public static final int TUESDAY = Calendar.TUESDAY;
        public static final int WEDNESDAY = Calendar.WEDNESDAY;
        public static final int THURSDAY = Calendar.THURSDAY;
        public static final int FRIDAY = Calendar.FRIDAY;
        public static final int SATURDAY = Calendar.SATURDAY;//7
    }

    /**
     * Checks if the given date is before the current date (ignoring time).
     *
     * @param invoiceDate The date to be compared.
     * @return {@code true} if the invoice date is before today; {@code false} otherwise.
     */
    public static boolean isDateBeforeToday(Date invoiceDate) {
        // Get the current date without the time
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            today = sdf.parse(sdf.format(today));
            invoiceDate = sdf.parse(sdf.format(invoiceDate)); // Format invoiceDate
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

        // Compare only the dates (ignoring the time)
        return invoiceDate.before(today);
    }
}
