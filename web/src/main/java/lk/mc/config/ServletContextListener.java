package lk.mc.config;

import lk.mc.core.message.MqttManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Date;

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


    @PreDestroy
    public void destroy() {
        logger.info("Callback triggered - @PreDestroy.");

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
        //        BackgroundJob.delete(JOB_ID_RECORDING);
//        System.out.println(ANSI_BLUE + JOB_ID_RECORDING + " removed" + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "Removing Recurring Jobs - end" + ANSI_RESET);

        System.out.println();
    }
}