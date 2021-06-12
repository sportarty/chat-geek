package ru.gb.chat.server;

import java.sql.*;
import java.util.List;

/**
 * Created by Artem Kropotov on 24.05.2021
 */
public class DBAuthService implements AuthService<User>{

    private static DBAuthService INSTANCE;

    public static DBAuthService getInstance() throws SQLException {
        if (INSTANCE == null) {
            synchronized (DBAuthService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBAuthService();
                }
            }
        }
        return INSTANCE;
    }

    private final PreparedStatement psFindByLoginAndPassword;
    private final PreparedStatement psFindByLoginOrNick;
    private final PreparedStatement psSave;
    private final PreparedStatement psRemove;




    public DBAuthService() throws SQLException {
        psFindByLoginAndPassword = DBServise.createPreparedStatement("SELECT login, password, nickname FROM USERS WHERE login = ? AND password = ?;");
        psFindByLoginOrNick = DBServise.createPreparedStatement("SELECT login, password, nickname FROM USERS WHERE login = ? OR nickname = ?;");
        psSave = DBServise.createPreparedStatement("INSERT INTO users (login, password, nickname) VALUES(?,?,?);");
        psRemove = DBServise.createPreparedStatement("DELETE FROM users WHERE login = ? AND password = ? AND nickname = ?;");
    }


    @Override
    public User findByLoginAndPassword(String login, String password) {

        try {
            psFindByLoginAndPassword.setString(1,login);
            psFindByLoginAndPassword.setString(2,password);
            ResultSet resultSet = psFindByLoginAndPassword.executeQuery();
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
        try {
            psFindByLoginOrNick.setString(1,login);
            psFindByLoginOrNick.setString(3,nick);
            ResultSet resultSet = psFindByLoginOrNick.executeQuery();
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
        try {
            psSave.setString(1, user.getLogin());
            psSave.setString(2, user.getPassword());
            psSave.setString(3, user.getNickname());
            psSave.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }


    @Override
    public User remove(User user) {

        try {
            psRemove.setString(1, user.getLogin());
            psRemove.setString(2, user.getPassword());
            psRemove.setString(3, user.getLogin());
            psRemove.executeUpdate();
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
