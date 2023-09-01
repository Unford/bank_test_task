package by.clevertec.bank.controller.listener;

import by.clevertec.bank.config.AppConfiguration;
import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The ServletContextListenerImpl class is a Java class that implements the ServletContextListener interface and is
 * responsible for initializing and destroying the servlet context, as well as scheduling and running an accrual task at a
 * fixed rate.
 */
@WebListener
public class ServletContextListenerImpl implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger();

    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AppConfiguration.getInstance();
        ConnectionPool.getInstance();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new AccrualTask(), 0, 30, TimeUnit.SECONDS);
        logger.info("Context successfully loaded");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
        ConnectionPool.close();
    }

    /**
     * The AccrualTask class is a Runnable implementation that performs an accrual task by calling the accrueIncome method
     * of the AccountService.
     */
    private static class AccrualTask implements Runnable {
        private static final Logger logger = LogManager.getLogger();

        @Override
        public void run() {
            logger.debug("Accrual task is working");
            AccountService transactionService = AccountServiceImpl.getInstance();
            try {
                transactionService.accrueIncome(AppConfiguration
                        .getInstance()
                        .getBusiness()
                        .getMonthAccrual());
            } catch (ServiceException e) {
                logger.error(e);
            }
        }
    }
}
