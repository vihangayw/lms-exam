package lk.mc.config;

import lk.mc.core.message.MqttManager;
import lk.mc.std.bean.ExamPic;
import lk.mc.std.bean.ExamPreflightAudit;
import lk.mc.std.repository.ExamPreflightAuditRepository;
import lk.mc.std.repository.StudentQuizRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.scheduling.BackgroundJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.List;

import static lk.mc.core.util.TsStringUtils.*;


/**
 * Spring supports both the component-level and the context-level shutdown callbacks. We can create these callbacks using:
 * <p>
 * - @PreDestroy
 * - DisposableBean interface
 * - Bean-destroy method
 * - Global ServletContextListener
 * <p>
 * During the bean initialization, Spring will register all the bean methods that are annotated with @PreDestroy and
 * invokes them when the application shuts down.
 */
@SuppressWarnings("Duplicates")
@Component
public class ServletContextListener {

    private static Logger logger = LogManager.getLogger(ServletContextListener.class);

    @Autowired
    private ExamPreflightAuditRepository examPreflightAuditRepository;
    @Autowired
    private StudentQuizRepository studentQuizRepository;


    @PreDestroy
    public void destroy() {
        logger.info("Callback triggered - @PreDestroy.");

        // Save audit logs before shutdown
        saveAuditLogsOnShutdown();
        saveImagesOnShutdown();

        MqttManager.getMangerInstance().disconnect();

        System.out.println();
        System.out.print(ANSI_RED + "    _  _      _____ _           _      _ _                             ________   \n" +
                "  _| || |_   / ____| |         | |    | | |                           / / /\\ \\ \\  \n" +
                " |_  __  _| | (___ | |__  _   _| |_ __| | |__   _____      ___ __    / / /  \\ \\ \\ \n" +
                "  _| || |_   \\___ \\| '_ \\| | | | __/ _` | '_ \\ / _ \\ \\ /\\ / / '_ \\  < < <    > > >\n" +
                " |_  __  _|  ____) | | | | |_| | || (_| | | | | (_) \\ V  V /| | | |  \\ \\ \\  / / / \n" +
                "   |_||_|   |_____/|_| |_|\\__,_|\\__\\__,_|_| |_|\\___/ \\_/\\_/ |_| |_|   \\_\\_\\/_/_/  \n" +
                "            " + ANSI_RESET);
        System.out.println(ANSI_YELLOW + new Date() + ANSI_RESET);

        System.out.println();
        System.out.println(ANSI_PURPLE + "Removing Recurring Jobs - start" + ANSI_RESET);
        BackgroundJob.delete("save_logs");
        System.out.println(ANSI_BLUE + "save_logs" + " removed" + ANSI_RESET);
        BackgroundJob.delete("save_img");
        System.out.println(ANSI_BLUE + "save_img" + " removed" + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "Removing Recurring Jobs - end" + ANSI_RESET);

        System.out.println();
    }

    /**
     * Save audit logs from AUDIT_LIST to database before application shutdown
     */
    private void saveAuditLogsOnShutdown() {
        try {
            List<ExamPreflightAudit> logsToSave = JobRunnerScheduleStarter.getAuditEntries();

            if (logsToSave.isEmpty()) {
                logger.info("No audit logs to save on shutdown. AUDIT_LIST is empty.");
                return;
            }

            logger.info("Application shutdown detected. Saving {} audit log entries to database", logsToSave.size());

            // Save all logs to database
            examPreflightAuditRepository.saveAll(logsToSave);

            // Clear the in-memory list after successful save
            JobRunnerScheduleStarter.clearAuditList();

            logger.info("Successfully saved {} audit log entries to database on shutdown", logsToSave.size());
        } catch (Exception e) {
            logger.error("Error saving audit logs to database on shutdown", e);
            // Log error but don't throw - we're shutting down anyway
        }
    }

    private void saveImagesOnShutdown() {
        try {
            List<ExamPic> logsToSave = JobRunnerScheduleStarter.getImgList();

            if (logsToSave.isEmpty()) {
                logger.info("No images to save on shutdown. IMG_LIST is empty.");
                return;
            }

            logger.info("Application shutdown detected. Saving {} image entries to database", logsToSave.size());

            // Save all logs to database
            studentQuizRepository.saveAll(logsToSave);

            // Clear the in-memory list after successful save
            JobRunnerScheduleStarter.clearImgList();

            logger.info("Successfully saved {} image entries to database and cleared IMG_LIST", logsToSave.size());
        } catch (Exception e) {
            logger.error("Error saving audit logs to database on shutdown", e);
            // Log error but don't throw - we're shutting down anyway
        }
    }
}