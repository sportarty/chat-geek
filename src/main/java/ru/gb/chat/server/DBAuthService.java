package ru.gb.chat.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Artem Kropotov on 24.05.2021
 */
public class DBAuthService  implements AuthService<User>  {

    private static DBAuthService INSTANCE;

    public static DBAuthService getInstance() {
        if (INSTANCE == null) {
            synchronized (DBAuthService.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = new DBAuthService();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return INSTANCE;
    }

    private final PreparedStatement psFindByLoginOrNick;
    private final PreparedStatement psFindByLoginAndPassword;
    private final PreparedStatement psSaveUser;
    private final PreparedStatement psDeleteUser;

    public DBAuthService() throws SQLException {
        psFindByLoginOrNick =
                DBService.createPreparedStatement("SELECT * FROM users WHERE login = ? OR nickname = ?;");
        psFindByLoginAndPassword =
                DBService.createPreparedStatement("SELECT * FROM users WHERE login = ? AND password = ?;");
        psSaveUser =
                DBService.createPreparedStatement("INSERT INTO users(login, password, nickname) VALUES (?, ?, ?);");
        psDeleteUser =
                DBService.createPreparedStatement("DELETE FROM users WHERE login=? AND password=? AND nickname=?;");
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        try {
            psFindByLoginAndPassword.setString(1, login);
            psFindByLoginAndPassword.setString(2, password);
            ResultSet resultSet = psFindByLoginAndPassword.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new User(
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("nick")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLoginOrNick(String login, String nick) {
        try {
            psFindByLoginOrNick.setString(1, login);
            psFindByLoginOrNick.setString(2, nick);
            ResultSet resultSet = psFindByLoginOrNick.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new User(
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("nickname")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        try {
            psSaveUser.setString(1, user.getLogin());
            psSaveUser.setString(2, user.getPassword());
            psSaveUser.setString(3, user.getNickname());
            int raws = psSaveUser.executeUpdate();
            if (raws == 1) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User remove(User user) {
        try {
            psDeleteUser.setString(1, user.getLogin());
            psDeleteUser.setString(2, user.getPassword());
            psDeleteUser.setString(3, user.getNickname());
            int raws = psDeleteUser.executeUpdate();
            if (raws == 1) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
