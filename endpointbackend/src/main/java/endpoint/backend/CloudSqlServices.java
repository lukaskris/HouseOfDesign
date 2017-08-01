package endpoint.backend;


import com.google.appengine.api.utils.SystemProperty;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;

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

    public Connection getConnection() throws Exception {
        try {
//             Class.forName("com.mysql.jdbc.GoogleDriver");
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e) {
            throw new ServletException("Error loading Google JDBC Driver", e);
        }

        String url = "jdbc:mysql://104.155.207.230:3306/houseofdesign";

//        if (SystemProperty.environment.value() ==
//                SystemProperty.Environment.Value.Production) {
//            // Load the class that provides the new "jdbc:google:mysql://" prefix.
//            Class.forName("com.mysql.jdbc.GoogleDriver");
//            url = "jdbc:google:mysql://your-project-id:your-instance-name/guestbook?user=root";
//        } else {
//            // Local MySQL instance to use during development.
//            Class.forName("com.mysql.jdbc.Driver");
//            url = "jdbc:mysql://127.0.0.1:3306/guestbook?user=root";
//        }

        Connection connection = DriverManager.getConnection(url, "root", "lukask10tki");
        return connection;
    }
}
