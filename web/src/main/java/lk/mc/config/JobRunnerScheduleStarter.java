package lk.mc.config;

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;

/**
 * This class deals with the jobs schedulers & recurring schedules
 *
 * @author vihangawicks
 * @since 14/11/22
 * MC-lms
 */
// */10 * * * * * - 10 sec
// */10 * * * * - 10 min
@Configuration
@Singleton
@Service
@NoArgsConstructor
public class JobRunnerScheduleStarter {

//    public static final Map<String, Integer> OTP_MAP = new HashMap<>();
    private static Logger logger = LogManager.getLogger(JobRunnerScheduleStarter.class);
    @Autowired
    private Environment environment;


//    @Recurring(id = "reset_otp", cron = "10 */5 * * * *")
//    @Job(name = "Reset OTP")
//    public void reset_otp() {
//        if (!OTP_MAP.isEmpty()) OTP_MAP.clear();
//    }

}