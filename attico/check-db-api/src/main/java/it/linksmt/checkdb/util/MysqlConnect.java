package it.linksmt.checkdb.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MysqlConnect {
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://#HOST#:#PORT#/#DB_NAME#";

    private Connection connection;
    private Properties properties;

    public Connection connect(String username, String password, String host, String port, String dbName) {
        if (connection == null) {
            try {
            	properties = new Properties();
                properties.setProperty("user", username);
                properties.setProperty("password", password);
                Class.forName(DATABASE_DRIVER);
                String url = DATABASE_URL.replace("#HOST#", host).replace("#PORT#", port).replace("#DB_NAME#", dbName);
                connection = DriverManager.getConnection(url, properties);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}