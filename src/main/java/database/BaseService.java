package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseService {

    private Connection connection;
    private final String DB_URL;

    protected BaseService(String dbDriver, String dbUrl) {
        DB_URL = dbUrl;
        try {
            Class.forName(dbDriver);
            System.out.println("Driver initialize");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Driver initialization caused an error");
        }
    }

    protected void openConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("The connection is opened");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("The connection could not be opened");
        }
    }

    protected void closeConnection() {
        try {
            connection.close();
            System.out.println("The connection is closed");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("The connection could not be closed");
        }
    }

    protected Statement getStatement(){
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Statement is not created");
        }
        return null;
    }
}
