package by.clevertec.bank.controller.listener;

import by.clevertec.bank.config.AppConfiguration;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

@WebListener
public class ServletContextListenerImpl implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //todo open datasource
        AppConfiguration.getInstance();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //todo close datasource
    }
}
