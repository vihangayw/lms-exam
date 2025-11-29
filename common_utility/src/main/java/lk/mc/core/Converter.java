package lk.mc.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Converter converts a String object into a Date object
 *
 * @author vihanga
 * @since 28/10/2021
 * MC-lms
 */
public class Converter {

    private static Logger logger = LogManager.getLogger(Converter.class);

    private Converter() {
        //Implicitly added for compliant with sonar rule 'Utility classes should not have public constructors'
    }

    /**
     * stringToDate method converts a String object received from lk.mc.feedback.api into a Date object,
     * and pass it into the core, along with the function request bean.
     *
     * @param dateInString   date object as a string object
     * @param dateTimeFormat date format of string object
     * @return date object
     */
    public static Date stringToDate(String dateInString, String dateTimeFormat) {

        SimpleDateFormat df = new SimpleDateFormat(dateTimeFormat);
        Date date;
        try {
            date = df.parse(dateInString);
            logger.info("DateTime >> " + date);
            return date;
        } catch (ParseException e) {
            logger.info("Error in converting the String to a valid Date-Time format >> " + e.getMessage(), e);
            date = null;
            return date;
        }
    }

    /**
     * timeToUTC method converts a Date object into a Date object with UTC zone,
     * This is useful to calculate time values.
     *
     * @param date java.util.Date object
     * @return date object (HH:mm:ss)
     */
    public static Date timeToUTC(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return simpleDateFormat.parse(String.valueOf(date));
        } catch (Exception e) {
            logger.info("Error in converting the java.util.Date to a valid Time format >> " + e.getMessage(), e);
            date = null;
            return date;
        }
    }

    public static String timeDiff(Date start, Date end) throws ParseException {
        logger.info("Start Time | {}", start);
        logger.info("End Time   | {}", end);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        long difference = format.parse(format.format(end)).getTime() - format.parse(format.format(start)).getTime();

        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(difference - 19800000);// 5 h and 30 s
        instance.setTimeZone(TimeZone.getTimeZone("Asia/Colombo"));

        Date diff = instance.getTime();
        logger.info("Time Diff  | {}", diff);

        return format.format(diff);
    }

    public static boolean checkTimeDiff(Date currentTime, Date subTime, long seconds) throws ParseException {
        logger.info("Current Time | {}", currentTime);
        logger.info("Subtracted Time   | {}", subTime);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        long difference = format.parse(format.format(currentTime)).getTime() - format.parse(format.format(subTime)).getTime();

        long timeSecs = TimeUnit.MILLISECONDS.toSeconds(difference);
        logger.info("Time Diff  | {}", timeSecs);
        return timeSecs <= seconds;
    }
}