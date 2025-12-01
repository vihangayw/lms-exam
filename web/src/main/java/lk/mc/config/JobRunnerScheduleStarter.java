package lk.mc.config;

import lk.mc.std.bean.ExamPic;
import lk.mc.std.bean.ExamPreflight;
import lk.mc.std.bean.ExamPreflightAudit;
import lk.mc.std.repository.ExamPreflightAuditRepository;
import lk.mc.std.repository.ExamPreflightRepository;
import lk.mc.std.repository.StudentQuizRepository;
import lk.mc.std.util.Constants;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.spring.annotations.Recurring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;
import java.io.File;
import java.util.*;

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

    public static final List<ExamPreflightAudit> AUDIT_LIST = Collections.synchronizedList(new ArrayList<>());
    public static final List<ExamPic> IMG_LIST = Collections.synchronizedList(new ArrayList<>());
    private static final Logger logger = LogManager.getLogger(JobRunnerScheduleStarter.class);
    //    @Autowired
//    private Environment environment;
    @Autowired
    private ExamPreflightAuditRepository examPreflightAuditRepository;
    @Autowired
    private StudentQuizRepository studentQuizRepository;
    @Autowired
    private ExamPreflightRepository examPreflightRepository;

    /**
     * Add audit entry to the list with logging
     *
     * @param audit ExamPreflightAudit entry to add
     */
    public static synchronized void addAuditEntry(ExamPreflightAudit audit) {
        if (audit != null) {
            AUDIT_LIST.add(audit);
            logger.info("Audit entry added to AUDIT_LIST | QR: {} | SQID: {} | Description: {} | List size: {}",
                    audit.getQr() != null ? audit.getQr() : "N/A",
                    audit.getSqid() != null ? audit.getSqid() : "N/A",
                    audit.getDescription() != null ? audit.getDescription() : "N/A",
                    AUDIT_LIST.size());
        } else {
            logger.warn("Attempted to add null audit entry to AUDIT_LIST");
        }
    }

    public static synchronized void addImgEntry(ExamPic examPic) {
        if (examPic != null) {
            IMG_LIST.add(examPic);
            logger.info("Pic Added | SQID: {} | CAM: {} | List size: {}", examPic.getSqId(), examPic.isCam(),
                    IMG_LIST.size());
        } else {
            logger.warn("Attempted to add null audit entry to IMG_LIST");
        }
    }

    /**
     * Get all audit entries from the list with logging
     *
     * @return List of audit entries
     */
    public static synchronized List<ExamPreflightAudit> getAuditEntries() {
        return new ArrayList<>(AUDIT_LIST);
    }

    public static synchronized List<ExamPic> getImgList() {
        return new ArrayList<>(IMG_LIST);
    }

    /**
     * Clear audit list with logging
     */
    public static synchronized void clearAuditList() {
        int size = AUDIT_LIST.size();
        AUDIT_LIST.clear();
        logger.info("AUDIT_LIST cleared | Previous size: {} | Entries removed", size);
    }

    public static synchronized void clearImgList() {
        int size = IMG_LIST.size();
        IMG_LIST.clear();
        logger.info("IMG_LIST cleared | Previous size: {} | Entries removed", size);
    }


    /**
     * Scheduled job to save audit logs from in-memory list to database
     * Runs every 30 minutes
     */
    @Recurring(id = "save_logs", cron = "8 */30 * * * *")
    @Job(name = "Save Logs")
    public void save_logs() {
        try {
            List<ExamPreflightAudit> logsToSave = getAuditEntries();

            if (logsToSave.isEmpty()) {
                logger.debug("No audit logs to save. AUDIT_LIST is empty.");
                return;
            }

            logger.info("Saving {} audit log entries to database", logsToSave.size());

            // Save all logs to database
            examPreflightAuditRepository.saveAll(logsToSave);

            // Clear the in-memory list after successful save
            clearAuditList();

            logger.info("Successfully saved {} audit log entries to database and cleared AUDIT_LIST", logsToSave.size());
        } catch (Exception e) {
            logger.error("Error saving audit logs to database", e);
            // Don't clear the list if save failed, so logs can be retried
        }
    }

    @Recurring(id = "save_img", cron = "40 */3 * * * *")
    @Job(name = "Save Pics")
    public void save_pics() {
        try {
            List<ExamPic> logsToSave = getImgList();

            if (logsToSave.isEmpty()) {
                return;
            }

            logger.info("Saving {} pic entries to database", logsToSave.size());

            // Save all logs to database
            studentQuizRepository.saveAll(logsToSave);

            // Clear the in-memory list after successful save
            clearImgList();

            logger.info("Successfully saved {} image entries to database and cleared IMG_LIST", logsToSave.size());
        } catch (Exception e) {
            logger.error("Error saving audit logs to database", e);
            // Don't clear the list if save failed, so logs can be retried
        }
    }

    /**
     * Scheduled job to clean up old data (images and database records)
     * Runs on the 1st of every month at 01:00 AM
     * Deletes data that is 4 months or older
     */
    @Recurring(id = "cleanup_old_data", cron = "1 1 1 1 * *")
    @Job(name = "Cleanup Old Data")
    public void cleanup_old_data() {
        try {
            logger.info("Starting cleanup job for data older than 4 months");

            // Calculate cutoff date (4 months ago)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -4);
            Date cutoffDate = calendar.getTime();

            logger.info("Cutoff date for cleanup: {}", cutoffDate);

            // 1. Clean up ExamPic records and associated images
            int deletedPics = cleanupExamPics(cutoffDate);

            // 2. Clean up ExamPreflightAudit records
            int deletedAudits = cleanupExamPreflightAudits(cutoffDate);

            // 3. Clean up ExamPreflight records and associated images
            int deletedPreflights = cleanupExamPreflights(cutoffDate);

            logger.info("Cleanup job completed successfully | Deleted ExamPic: {} | Deleted Audit: {} | Deleted Preflight: {}",
                    deletedPics, deletedAudits, deletedPreflights);

        } catch (Exception e) {
            logger.error("Error during cleanup job", e);
        }
    }

    /**
     * Clean up ExamPic records and associated image files
     * Deletes entire quiz/{sqid} directories instead of individual files
     *
     * @param cutoffDate Records older than this date will be deleted
     * @return Number of records deleted
     */
    private int cleanupExamPics(Date cutoffDate) {
        try {
            List<ExamPic> oldPics = studentQuizRepository.findOlderThan(cutoffDate);
            logger.info("Found {} ExamPic records older than cutoff date", oldPics.size());

            if (oldPics.isEmpty()) {
                return 0;
            }

            // Group old records by sqId
            Map<Integer, List<ExamPic>> picsBySqId = new HashMap<>();
            for (ExamPic pic : oldPics) {
                if (pic.getSqId() != null) {
                    picsBySqId.computeIfAbsent(pic.getSqId(), k -> new ArrayList<>()).add(pic);
                }
            }

            int deletedCount = 0;
            int deletedDirs = 0;
            Set<Integer> processedSqIds = new HashSet<>();

            // For each sqId, check if ALL records for that sqId are being deleted
            for (Map.Entry<Integer, List<ExamPic>> entry : picsBySqId.entrySet()) {
                Integer sqId = entry.getKey();
                List<ExamPic> oldPicsForSqId = entry.getValue();

                try {
                    // Check if there are any records for this sqId that are NOT being deleted (newer than cutoff)
                    List<ExamPic> allPicsForSqId = studentQuizRepository.findBySqId(sqId);
                    boolean allRecordsAreOld = allPicsForSqId.size() == oldPicsForSqId.size();

                    if (allRecordsAreOld) {
                        // All records for this sqId are old - delete entire directory
                        String sqIdDirPath = Constants.SERVER_LOCAL_PATH
                                .concat("quiz/")
                                .concat(String.valueOf(sqId));
                        File sqIdDir = new File(sqIdDirPath);

                        if (sqIdDir.exists() && sqIdDir.isDirectory()) {
                            int filesDeleted = deleteDirectory(sqIdDir);
                            if (filesDeleted > 0) {
                                deletedDirs++;
                                logger.info("Deleted entire directory for sqId {}: {} files/directories", sqId, filesDeleted);
                            }
                        }

                        // Delete all database records for this sqId
                        studentQuizRepository.deleteAll(oldPicsForSqId);
                        deletedCount += oldPicsForSqId.size();
                        processedSqIds.add(sqId);
                    } else {
                        // Some records are newer - delete old records from database but keep directory
                        // (Directory contains newer images, so we preserve it)
                        for (ExamPic pic : oldPicsForSqId) {
                            try {
                                if (pic != null) {
                                    studentQuizRepository.delete(pic);
                                    deletedCount++;
                                }
                            } catch (Exception e) {
                                logger.error("Error deleting ExamPic record ID: {}", pic != null ? pic.getId() : "null", e);
                            }
                        }
                        logger.debug("Partial cleanup for sqId {}: {} old records deleted, newer records preserved",
                                sqId, oldPicsForSqId.size());
                    }
                } catch (Exception e) {
                    logger.error("Error processing sqId: {}", sqId, e);
                    // Fallback: delete records individually
                    for (ExamPic pic : oldPicsForSqId) {
                        try {
                            if (!processedSqIds.contains(pic.getSqId())) {
                                studentQuizRepository.delete(pic);
                                deletedCount++;
                            }
                        } catch (Exception ex) {
                            logger.error("Error deleting ExamPic record ID: {}", pic.getId(), ex);
                        }
                    }
                }
            }

            logger.info("Deleted {} ExamPic records and {} entire directories", deletedCount, deletedDirs);
            return deletedCount;
        } catch (Exception e) {
            logger.error("Error cleaning up ExamPic records", e);
            return 0;
        }
    }

    /**
     * Clean up ExamPreflightAudit records
     *
     * @param cutoffDate Records older than this date will be deleted
     * @return Number of records deleted
     */
    private int cleanupExamPreflightAudits(Date cutoffDate) {
        try {
            List<ExamPreflightAudit> oldAudits = examPreflightAuditRepository.findOlderThan(cutoffDate);
            logger.info("Found {} ExamPreflightAudit records older than cutoff date", oldAudits.size());

            examPreflightAuditRepository.deleteAll(oldAudits);
            logger.info("Deleted {} ExamPreflightAudit records", oldAudits.size());
            return oldAudits.size();
        } catch (Exception e) {
            logger.error("Error cleaning up ExamPreflightAudit records", e);
            return 0;
        }
    }

    /**
     * Clean up ExamPreflight records and associated image files
     *
     * @param cutoffDate Records older than this date will be deleted
     * @return Number of records deleted
     */
    private int cleanupExamPreflights(Date cutoffDate) {
        try {
            List<ExamPreflight> oldPreflights = examPreflightRepository.findOlderThan(cutoffDate);
            logger.info("Found {} ExamPreflight records older than cutoff date", oldPreflights.size());

            int deletedCount = 0;
            int deletedFiles = 0;

            for (ExamPreflight preflight : oldPreflights) {
                try {
                    // Delete associated image files and directory
                    if (preflight.getReference() != null) {
                        String preflightDir = Constants.SERVER_LOCAL_PATH
                                .concat("exam-preflight/")
                                .concat(preflight.getReference());
                        File directory = new File(preflightDir);
                        if (directory.exists() && directory.isDirectory()) {
                            deletedFiles += deleteDirectory(directory);
                            logger.debug("Deleted preflight directory: {}", preflightDir);
                        }
                    }

                    // Delete database record
                    examPreflightRepository.delete(preflight);
                    deletedCount++;
                } catch (Exception e) {
                    logger.error("Error deleting ExamPreflight record ID: {}", preflight.getId(), e);
                }
            }

            logger.info("Deleted {} ExamPreflight records and {} files/directories", deletedCount, deletedFiles);
            return deletedCount;
        } catch (Exception e) {
            logger.error("Error cleaning up ExamPreflight records", e);
            return 0;
        }
    }

    /**
     * Delete a directory and all its contents
     *
     * @param directory Directory to delete
     * @return Number of files deleted
     */
    private int deleteDirectory(File directory) {
        int deletedCount = 0;
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deletedCount += deleteDirectory(file);
                    } else {
                        if (file.delete()) {
                            deletedCount++;
                        }
                    }
                }
            }
            // Delete the directory itself
            if (directory.delete()) {
                deletedCount++;
            }
        }
        return deletedCount;
    }


}