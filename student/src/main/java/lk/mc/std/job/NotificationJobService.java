package lk.mc.std.job;

import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Component;

/**
 * Declaration of methods which will be implemented by UserJobServiceImpl class.
 *
 * @author vihangawicks
 * @created 06/10/22
 * MC-lms - Queue
 */
@Component
public interface NotificationJobService {

    String NOTIFICATION_BOT = "Notification Sender";

    @Job(name = NOTIFICATION_BOT + " QUIZ IMG")
    public void quizImg(int studentQuizId, String imgName);

    @Job(name = NOTIFICATION_BOT + " QUIZ SCRN")
    public void quizScr(int studentQuizId, String imgName);

    @Job(name = NOTIFICATION_BOT + " QUIZ PreFlight Scan")
    public void preFlightScan(String qr);

    @Job(name = NOTIFICATION_BOT + " QUIZ PreFlight Upload")
    public void preFlightUpload(String qr);


}
