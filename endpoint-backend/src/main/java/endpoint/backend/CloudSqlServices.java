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

//        try {
//             Class.forName("com.mysql.jdbc.GoogleDriver");
////            Class.forName("com.mysql.jdbc.Driver");
//
//        }catch (ClassNotFoundException e) {
//            throw new ServletException("Error loading Google JDBC Driver", e);
//        }

        String url = "";
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            // Load the class that provides the new "jdbc:google:mysql://" prefix.
            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
            }catch (ClassNotFoundException e){
                throw new ServletException("Error loading Google JDBC Driver", e);
            }
            url = "jdbc:google:mysql://default-demo-app-db53e:asia-east1:houseofdesign/houseofdesign?user=root";
        } else {
            // Local MySQL instance to use during development.
//                Class.forName("com.google.appengine.api.rdbms.AppEngineDriver");
            try {
                Class.forName("com.mysql.jdbc.Driver");
//                Class.forName("com.mysql.jdbc.GoogleDriver");
            }catch (ClassNotFoundException e){
                throw new ServletException("Error loading JDBC Driver", e);
            }
            url = "jdbc:mysql://104.155.207.230:3306/houseofdesign?user=root";
//            url = "jdbc:google:mysql://default-demo-app-db53e:asia-east1:houseofdesign/houseofdesign?user=root";
        }


        Connection connection = DriverManager.getConnection(url, "root", "lukask10tki");
        return connection;
    }
}
