package ru.gb.chat.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBService {

    private static String DB_URL = "jdbc:postgresql://127.0.0.1:5432/gb";
    private static String USER = "gbChat";
    private static String PASS = "12345";

    private static Connection connection;

    public static PreparedStatement createPreparedStatement(String request) throws SQLException {
        return connection.prepareStatement(request);
    }

    public static void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        System.out.println("PostgreSQL JDBC Driver successfully connected");
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Success connected to DB");
    }

    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
