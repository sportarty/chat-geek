package ru.gb.chat.server;

import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBLogger implements Logger <Pair<String,String>> {
    private Statement stmt;
    private PreparedStatement addStatement;
    private PreparedStatement getStatement;
    private Connection connection;
    public DBLogger() {
        try {
            connect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public boolean add(Pair<String,String> nick_message) {
        try {
            addStatement.setString(1,nick_message.getKey());
            addStatement.setString(2, nick_message.getValue());
            ResultSet rs = addStatement.executeQuery();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
    public List<Pair<String,String>> getNumLast(int num){
        try {
            getStatement.setInt(1, num);
            ArrayList<Pair<String,String>> retArr = new ArrayList<>();

            try (ResultSet rs = getStatement.executeQuery()) {
                while (rs.next()) {
                    retArr.add(new Pair<>(rs.getString("nick"),rs.getString("message")) );
                }
            }
            return retArr;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
    public void close(){
        disconnect();
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:log.db");
        stmt = connection.createStatement();
        createTableEx();
        addStatement = connection.prepareStatement("INSERT INTO log (nick, message) VALUES (?, ?)");
        getStatement = connection.prepareStatement("SELECT * FROM log ORDER BY ID DESC LIMIT ?");
    }
    public void disconnect(){
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (addStatement != null) {
                addStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (getStatement != null) {
                getStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableEx() throws SQLException {
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS log (\n" +
                "        id    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "        nick  TEXT,\n" +
                "        message TEXT\n" +
                "    );");
    }


}
