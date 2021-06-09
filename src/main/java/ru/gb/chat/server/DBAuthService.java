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

    public DBAuthService(Connection connection1) throws SQLException {
        connection = connection1;
        statement = connection.createStatement();
    }

    public static DBAuthService getInstance(Connection conn) throws SQLException {
        if (INSTANCE == null) {
            synchronized (DBAuthService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBAuthService(conn);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public User findByLoginAndPassword(String login, String password) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM employee");
        while (resultSet.next()) {
            if(resultSet.getString(2).equals(login) & resultSet.getString(4).equals(password)) {
                return new User(resultSet.getString(2),resultSet.getString(4),resultSet.getString(3));
            }
        }
        return null;
    }

    @Override
    public User findByLoginOrNick(String login, String nick) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM employee");
        while (resultSet.next()) {                                          // перебор элементов
            if(resultSet.getString(2).equals(login) & resultSet.getString(3).equals(nick)) {
                return new User(resultSet.getString(2),resultSet.getString(4),resultSet.getString(3));
            }
        }
        return null;
    }

    @Override
    public User save(User user) throws SQLException {
        statement.executeUpdate(String.format("INSERT INTO employee (last_name, first_name, age) VALUES('%s','%s','%s');",user.getNickname(),user.getLogin(),user.getPassword()));
        return user;
    }

    @Override
    public User remove(User user) throws SQLException {
        statement.executeUpdate(String.format("DELETE INTO employee (last_name, first_name, age) VALUES('%s','%s','%s');",user.getNickname(),user.getLogin(),user.getPassword()));
        return user;
    }

    @Override
    public User changeNick(User user) throws SQLException {
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
