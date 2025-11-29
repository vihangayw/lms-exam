package lk.mc;

import lk.mc.config.ActiveMqConfiguration;
import lk.mc.config.ConfigurationManager;
import lk.mc.config.JobRunnerConfiguration;
import lk.mc.config.JobRunnerScheduleStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author vihangawicks
 * @since 11/4/21
 * MC-lms
 */

@SuppressWarnings("Duplicates")
@SpringBootApplication
@ComponentScan(basePackages = "lk.mc.*")
@EntityScan(basePackages = "lk.mc.*")
@EnableJpaRepositories(basePackages = "lk.mc.*")
@Import({JobRunnerConfiguration.class, ActiveMqConfiguration.class, ConfigurationManager.class, JobRunnerScheduleStarter.class})
public class LmsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LmsApplication.class);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(LmsApplication.class);
    }


}
