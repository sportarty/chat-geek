package ru.gb.chat.server;

import java.sql.*;
import java.util.List;

/**
 * Created by Artem Kropotov on 24.05.2021
 */
public class DBAuthService  implements AuthService<User>  {

    private static DBAuthService INSTANCE;
    private static Connection connection;
    private static Statement stmt;


    private DBAuthService() {
        try {
            connect();
            insertEx();
        } catch (Exception e) {
            e.printStackTrace();
            disconnect();
        } finally {
            disconnect();
        }
    }

    private static void insertEx() throws SQLException {
        for (int i = 0; i <= 10; i++) {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO employee (Login, Password, Nickname) VALUES (?, ?, ?);");
            pstmt.setString(1, "login" + i);
            pstmt.setString(2, "pass" + i);
            pstmt.setString(3, "nick" + i);
            new User("login" + i, "pass" + i, "nick" + i);
        }
    }





    public static DBAuthService getInstance() {
        if (INSTANCE == null) {
            synchronized (ListAuthService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBAuthService();
                }
            }
        }
        return INSTANCE;
    }

    private static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:demobase.db");
        stmt = connection.createStatement();
    }


    private static void disconnect() {
        try {
            if (stmt != null) {
                stmt.close();
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

    @Override
    public User findByLoginAndPassword(String login, String password) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT Login, Password, Nickname FROM employee WHERE Login=? AND Password=?");
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            ResultSet result = pstmt.executeQuery();
            if (!result.next()) {
                return null;
            } else {
                User user = new User(result.getString(1), result.getString(2), result.getString(3));
                return user;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }


    @Override
    public User findByLoginOrNick(String login, String nick) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT Login, Password, Nickname FROM employee WHERE Login=? AND Nickname=?");
            pstmt.setString(1, login);
            pstmt.setString(2, nick);
            ResultSet result = pstmt.executeQuery();
            if (!result.next()) {
                return null;
            } else {
                User user = new User(result.getString(1), result.getString(2), result.getString(3));
                return user;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    @Override
    public User save(User object) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO employee (Login, Password, Nickname)  VALUES (?,?,?)");
            pstmt.setString(1, object.getLogin());
            pstmt.setString(2, object.getPassword());
            pstmt.setString(3, object.getNickname());
            int result = pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return object;
    }

    @Override
    public User remove(User object) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM employee WHERE Login=?");
            pstmt.setString(1, object.getLogin());
            int result = pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public User removeById(Long aLong) {
        return null;
    }

    @Override
    public User findById(Long aLong) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }
}
