package com.example.lukaskris.houseofdesign.Services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Lukaskris on 29/07/2017.
 */

public class CloudSqlServices {
    private static CloudSqlServices instance;

    public static CloudSqlServices getInstance() {
        if (instance == null) {
            instance = new CloudSqlServices();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                        + "socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                "houseofdesign",
                "houseofdesign");

        Connection connection = DriverManager.getConnection(jdbcUrl, "root", "lukask10tki");
        return connection;
    }
}
