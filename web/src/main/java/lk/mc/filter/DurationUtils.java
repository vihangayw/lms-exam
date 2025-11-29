package lk.mc.filter;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtils {

    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([hdms])");

    public static Duration parseDuration(String duration) {
        Matcher matcher = DURATION_PATTERN.matcher(duration.toLowerCase());
        if (matcher.matches()) {
            long amount = Long.parseLong(matcher.group(1));
            char unit = matcher.group(2).charAt(0);

            switch (unit) {
                case 'h':
                    return Duration.ofHours(amount);
                case 'd':
                    return Duration.ofDays(amount);
                case 'm':
                    return Duration.ofMinutes(amount);
                case 's':
                    return Duration.ofSeconds(amount);
                default:
                    throw new IllegalArgumentException("Unknown duration unit: " + unit);
            }
        } else {
            throw new IllegalArgumentException("Invalid duration format: " + duration);
        }
    }
}
