package by.clevertec.bank.config;

import lombok.*;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.representer.Representer;


@Getter
@NoArgsConstructor
@ToString
public final class AppConfiguration {
    private static final String CONFIGURATION_FILENAME = "configuration.yml";
    private static AppConfiguration appConfiguration;

    private DatabaseConfig database;
    private BusinessConfig business;

    static {

    }


    public static AppConfiguration getInstance() {
        if (appConfiguration == null) {
            Representer representer = new Representer(new DumperOptions());
            representer.getPropertyUtils().setSkipMissingProperties(true);
            LoaderOptions loaderOptions = new LoaderOptions();
            Constructor constructor = new Constructor(AppConfiguration.class, loaderOptions);
            Yaml yaml = new Yaml(constructor, representer);
            yaml.setBeanAccess(BeanAccess.FIELD);
            appConfiguration = yaml.loadAs(AppConfiguration.class.getClassLoader()
                            .getResourceAsStream(CONFIGURATION_FILENAME),
                    AppConfiguration.class);

        }

        return appConfiguration;
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static final class DatabaseConfig {
        private String url;
        private String username;
        private String password;
        private String connector;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static final class BusinessConfig {
        private int monthAccrual;
    }
}
