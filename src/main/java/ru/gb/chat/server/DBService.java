package ru.gb.chat.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Artem Kropotov on 10.06.2021
 */
public class DBService {
    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/gb";
    private static final String USER = "gbChat";
    private static final String PASS = "12345";

    private static Connection connection;

    public static PreparedStatement createPreparedStatement(String request) throws SQLException {
        return connection.prepareStatement(request);
    }

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
