package lk.mc.config;

import lk.mc.core.util.TsStringUtils;
import lk.mc.std.bean.Configs;
import lk.mc.std.repository.ConfigRepository;
import lk.mc.std.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

import static lk.mc.core.util.ApplicationUtils.CONFIGS;

/**
 * Configures connectionfactory
 *
 * @author vihangawicks
 * @since 02/07/22
 * MC-lms
 */
@Configuration
public class ConfigurationManager {
    private static Logger log = LogManager.getLogger(ConfigurationManager.class);
     private final ConfigRepository repository;

    @Autowired
    public ConfigurationManager(@Qualifier("stdConfigRepository") ConfigRepository repository) {
        this.repository = repository;
    }

    private static void exc() {

        File baseFolder = new File(Constants.SERVER_LOCAL_PATH
                .concat("exam-preflight"));

        if (baseFolder.exists()) return;

        baseFolder.mkdir();
        new File(Constants.SERVER_LOCAL_PATH
                .concat("quiz")).mkdir();
    }

    @Bean
    public void init() {
        List<Configs> configsList = repository.findAll();

        System.out.println();
        log.info("............................................");
        if (configsList.isEmpty())
            log.info("Configurations are empty");
        else configsList.forEach(configs -> {
            CONFIGS.put(configs.getId(), configs.getVal());
            log.info(configs.getId().concat(" | ").concat(configs.getVal()));
        });
        log.info("............................................");
        System.out.println();

        String h = CONFIGS.get(Configs.SERVER_HIBERNATE);
        if (!TsStringUtils.isNullOrEmpty(h))
            Constants.SERVER_HIBERNATE = h;
        else
            System.out.println(TsStringUtils.ANSI_RED + ("SET SERVER_HIBERNATE") + TsStringUtils.ANSI_RESET);

        String localPath = CONFIGS.get(Configs.SERVER_LOCAL_PATH);
        if (!TsStringUtils.isNullOrEmpty(localPath))
            Constants.SERVER_LOCAL_PATH = localPath;
        else
            System.out.println(TsStringUtils.ANSI_RED + ("SET SERVER_LOCAL_PATH") + TsStringUtils.ANSI_RESET);

        String serverPath = CONFIGS.get(Configs.SERVER_BASE_URL);
        if (!TsStringUtils.isNullOrEmpty(serverPath))
            Constants.SEVER_BASE = serverPath;
        else
            System.out.println(TsStringUtils.ANSI_RED + ("SET SEVER_BASE") + TsStringUtils.ANSI_RESET);


        exc();
    }
}