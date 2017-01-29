package fi.exep.db;

import fi.exep.JerseyConfig;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;


public class Database {
    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setDriverClassName(JerseyConfig.getProperty("database.main.className"));
        dataSource.setUrl(JerseyConfig.getProperty("database.main.url"));
        dataSource.setUsername(JerseyConfig.getProperty("database.main.user"));
        dataSource.setPassword(JerseyConfig.getProperty("database.main.password"));
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
