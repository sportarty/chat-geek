package ru.gb.chat.server;

import java.sql.*;
import java.util.List;

/**
 * Created by Artem Kropotov on 24.05.2021
 */
public class DBAuthService  implements AuthService<User>  {

    private static DBAuthService INSTANCE;
    private static Connection connection;
    private static Statement statement;
    private static String URL = "jdbc:postgresql://localhost:5432/gb";
    private static String USER = "gbChat";
    private static String PASS = "12345";

    private DBAuthService () {
        try {
            connect();
        } catch (SQLException | ClassNotFoundException  throwables) {
            throwables.printStackTrace();
        }
    }

    public static void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(URL, USER, PASS);
        statement = connection.createStatement();
        System.out.println("Success connected to DB");
    }

    private static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static DBAuthService getInstance() {
        if (INSTANCE == null) {
            synchronized (DBAuthService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBAuthService();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public User findByLoginAndPassword(String login, String password) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT login, password, nick FROM users WHERE login=? AND password=?");
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            else {
                System.out.println(rs.getString(1));
                User user = new User(rs.getString(1),rs.getString(2),rs.getString(3));
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
            PreparedStatement ps = connection.prepareStatement("SELECT login, password,  FROM users WHERE login=? AND nickname=?");
            ps.setString(1, login);
            ps.setString(2, nick);
            ResultSet result = ps.executeQuery();
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
    public User updateNick(User user, String nick) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE users SET nickname=? WHERE login=? AND password=? AND nickname=?");
            ps.setString(1, nick);
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getNickname());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return findByLoginAndPassword(user.getLogin(), user.getPassword());
    }

    @Override
    public User save(User object) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users (login, password, nick)  VALUES (?,?,?)");
            ps.setString(1, object.getLogin());
            ps.setString(2, object.getPassword());
            ps.setString(3, object.getNickname());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return object;
    }

    @Override
    public User remove(User object) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE login=?");
            ps.setString(1, object.getLogin());
            ps.executeQuery();
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

