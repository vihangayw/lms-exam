package lk.mc.config;

import lk.mc.filter.DurationUtils;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.filters.RetryFilter;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.BackgroundJobServerConfiguration;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.BackgroundJobServerStatus;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.listeners.BackgroundJobServerStatusChangeListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * The JobRunr need storage to store the job details and support @{@link JobRunnerConfiguration} provides the storage.
 * JobRunr supports both SQL and NoSQL databases
 * <p>
 * By default, JobRunr will automatically create the necessary tables for your database.
 * If however you do not want to give the JobRunr DataSource DDL rights, you can easily create the tables JobRunr uses
 * yourself using one of the following methods:
 * <p>
 * InMemory - JobRunr comes with an InMemoryStorageProvider, which is ideal for lightweight tasks
 * that are server-instance specific and where persistence is not important. Note that if you use the
 * InMemoryStorageProvider, you can not scale horizontally as the storage is not shared.
 * <p>
 * <p>
 * For more information - https://www.jobrunr.io/en/documentation/installation/storage/
 * <p>
 * <p>
 * $$$$$$$$$$$$$$$$$$$$$$$$$$$ IMPORTANT $$$$$$$$$$$$$$$$$$$$$$$$$$$
 * IN CASE IF THERE IS AN ERROR IN SQL CREATE, MANUALLY ADD THE FOLLOWING SQL
 * INSERT INTO jobrunr_migrations (id, script, installedOn) VALUES ('8o567th6-c3p0-2ii9-kcuf-030vihanga07', 'v004__create_job_stats_view.sql', '2022-07-02T00:12:37.140177');
 *
 * @author vihangawicks
 * @since 02/07/22
 * MC-lms
 */
@Configuration
public class JobRunnerConfiguration {


    @Value("${org.jobrunr.background-job-server.permanently-delete-deleted-jobs-after}")
    private String permanentlyDeleteDeletedJobsAfter;
    @Value("${org.jobrunr.background-job-server.delete-succeeded-jobs-after}")
    private String deleteSucceededJobsAfter;
    @Value("${org.jobrunr.background-job-server.poll-interval-in-seconds}")
    private int pollIntervalInSeconds;
    @Value("${org.jobrunr.background-job-server.worker-count}")
    private int workerCount;

    /**
     * Define a javax.sql.DataSource and put the following code on startup:
     *
     * @param dataSource   sql ds
     * @param jobActivator autowired activator
     * @return {@link JobScheduler}
     */
    @Bean
    public JobScheduler initJobRunr(DataSource dataSource, JobActivator jobActivator) {


        // Initialize the HikariDataSource
        StorageProvider storageProvider;// = SqlStorageProviderFactory.using(dataSource);

//            System.out.println("// JobRunr configuration using In-Memory Storage");
            storageProvider = new InMemoryStorageProvider();

        JobScheduler jobScheduler = JobRunr.configure()
                .withJobFilter(new RetryFilter(0))
                .useJobActivator(jobActivator)
                .useStorageProvider(storageProvider)
//                .useStorageProvider(SqlStorageProviderFactory.using(hikariDataSource))
                .useBackgroundJobServer(createBackgroundJobServerConfig())
                .initialize()
                .getJobScheduler();

        JobRunr.getBackgroundJobServer().getStorageProvider().addJobStorageOnChangeListener(
                (BackgroundJobServerStatusChangeListener) changedServerStates -> {
                    for (BackgroundJobServerStatus serverStatus : changedServerStates) {
                        if (!serverStatus.isRunning()) {

                            JobRunr.getBackgroundJobServer().stop();

                            System.out.println("JobRunr stopped.");

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            JobRunr.getBackgroundJobServer().start(); // Start JobRunr again
                            System.out.println("JobRunr restarted.");
                        }
                    }
                });

        return jobScheduler;
    }

    private BackgroundJobServerConfiguration createBackgroundJobServerConfig() {
        return BackgroundJobServerConfiguration.usingStandardBackgroundJobServerConfiguration()
                .andWorkerCount(workerCount)
                .andPollIntervalInSeconds(pollIntervalInSeconds)
                .andDeleteSucceededJobsAfter(DurationUtils.parseDuration(deleteSucceededJobsAfter))
                .andPermanentlyDeleteDeletedJobsAfter(DurationUtils.parseDuration(permanentlyDeleteDeletedJobsAfter));
    }
}