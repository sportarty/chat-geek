package ru.gb.chat.server;

import java.sql.*;
import java.util.List;

/**
 * Created by Artem Kropotov on 24.05.2021
 */
public class DBAuthService  implements AuthService<User> {

    private static DBAuthService INSTANCE;

    private static String DB_URL="jdbc:postgresql://127.0.0.1:5433/gb";
    private static String USER="postgres";
    private static String PASS="";
    private static Connection connection;

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

    private DBAuthService() {

        try {
            connect();

        } catch (Exception e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public static void connect () throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        System.out.println("PostgreSQL JDBC Driver successfully connected");
        connection = DriverManager.getConnection(DB_URL,USER,PASS);
        System.out.println("Success connected to DB");
    }
    public static void disconnect () {
        if (connection!=null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }}
    }

    @Override
    public User findByLoginAndPassword(String login, String password) {

        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT login, password, nickname FROM USERS WHERE login = '%s' AND password = '%s'", login, password));
            {
                if (!resultSet.next()) return null;
                else {
                    User user = new User(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
                    return user;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();

        }
        return null;
    }

    @Override
    public User findByLoginOrNick(String login, String nick) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT login, password, nickname FROM USERS WHERE login = '%s' OR nickname = '%s'", login, nick));
            if (!resultSet.next()) return null;
            else {
                return new User(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public User save(User user) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(String.format("INSERT INTO users (login, password, nickname) VALUES('%s','%s','%s');", user.getLogin(), user.getPassword(), user.getNickname()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }


    @Override
    public User remove(User user) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(String.format("DELETE FROM users WHERE login = '%s' AND password = '%s' AND nickname = '%s';", user.getLogin(), user.getPassword(), user.getNickname()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
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
