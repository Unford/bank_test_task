package by.clevertec.bank.dao;

import by.clevertec.bank.config.AppConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public final class ConnectionPool {
    private static final Logger logger = LogManager.getLogger();

    private static final String DATABASE_NAME = "databaseName";
    private static final String PORT_NUMBER = "portNumber";
    private static final String SERVER_NAME = "serverName";
    private static final AtomicBoolean initCheck = new AtomicBoolean(false);
    private static final ReentrantLock initLock = new ReentrantLock(true);

    private static HikariDataSource dataSource;

    private ConnectionPool() {
    }

    public static HikariDataSource getInstance() {
        if (!initCheck.get()) {
            try {
                initLock.lock();
                if (dataSource == null) {
                    HikariConfig config = new HikariConfig();
                    AppConfiguration.DatabaseConfig configuration = AppConfiguration.getInstance().getDatabase();
                    config.setUsername(configuration.getUsername());
                    config.setPassword(configuration.getPassword());
                    config.setDataSourceClassName(configuration.getDataSourceClassName());
                    config.addDataSourceProperty(DATABASE_NAME, configuration.getDatabaseName());
                    config.addDataSourceProperty(PORT_NUMBER, configuration.getPortNumber());
                    config.addDataSourceProperty(SERVER_NAME, configuration.getServerName());
                    dataSource = new HikariDataSource(config);
                    initCheck.set(true);
                }

            } finally {
                initLock.unlock();
            }
        }
        return dataSource;
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    logger.error("SQL error while deregister driver", e);
                }
            });
        }
    }
}
