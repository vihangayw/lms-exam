package lk.mc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {
    private static Logger logger = LogManager.getLogger(Test.class);

    public static Double addValues(Double[] values) {
        double sum = 0.0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                return null;
            } else {
                sum += values[i];
            }
        }
        return sum;
    }


    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dayOfWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.MONTH, 1);

        System.out.println(dayOfWeekNumber);

        String format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(calendar.getTime());
        System.out.println(format);

        System.out.println(addValues(new Double[]{10d, 20d, 30d, 40d}));
        System.out.println(addValues(new Double[]{10d, null, 30d, 40d}));
        System.out.println(addValues(new Double[]{10.1d, 1.1, 30d, 40d}));
    }
}
