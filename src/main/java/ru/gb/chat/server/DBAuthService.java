package ru.gb.chat.server;

import java.sql.*;
import java.util.List;

/**
 * Created by Artem Kropotov on 24.05.2021
 */
public class DBAuthService  implements AuthService<User>  {

    private static DBAuthService INSTANCE;
    private final PreparedStatement psFindByLoginOrNick;
    private final PreparedStatement psFindByLoginAndPassword;
    private final PreparedStatement psSaveUser;
    private final PreparedStatement psDeleteUser;
    private final PreparedStatement psUpdateNick;


    private DBAuthService () throws SQLException {
        psFindByLoginOrNick =
                DBService.createPreparedStatement("SELECT login, password,  FROM users WHERE login=? OR nickname=?");
        psFindByLoginAndPassword =
                DBService.createPreparedStatement("SELECT login, password, nick FROM users WHERE login=? AND password=?");
        psSaveUser =
                DBService.createPreparedStatement("INSERT INTO users (login, password, nick)  VALUES (?,?,?)");
        psDeleteUser =
                DBService.createPreparedStatement("DELETE FROM users WHERE login=? AND password=? AND nickname=?");
        psUpdateNick =
                DBService.createPreparedStatement("UPDATE users SET nickname=? WHERE login=? AND password=? AND nickname=?");
    }


    public static DBAuthService getInstance() {
        if (INSTANCE == null) {
            synchronized (DBAuthService.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = new DBAuthService();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public User findByLoginAndPassword(String login, String password) {
        try {

            psFindByLoginAndPassword.setString(1, login);
            psFindByLoginAndPassword.setString(2, password);
            ResultSet rs = psFindByLoginAndPassword.executeQuery();
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
            psFindByLoginOrNick.setString(1, login);
            psFindByLoginOrNick.setString(2, nick);
            ResultSet result = psFindByLoginOrNick.executeQuery();
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

            psUpdateNick.setString(1, nick);
            psUpdateNick.setString(2, user.getLogin());
            psUpdateNick.setString(3, user.getPassword());
            psUpdateNick.setString(4, user.getNickname());
            psUpdateNick.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return findByLoginAndPassword(user.getLogin(), user.getPassword());
    }

    @Override
    public User save(User object) {
        try {
            psSaveUser.setString(1, object.getLogin());
            psSaveUser.setString(2, object.getPassword());
            psSaveUser.setString(3, object.getNickname());
            psSaveUser.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return object;
    }

    @Override
    public User remove(User object) {
        try {
            psDeleteUser.setString(1, object.getLogin());
            psDeleteUser.setString(2, object.getPassword());
            psDeleteUser.setString(3, object.getNickname());
            psDeleteUser.executeQuery();
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

