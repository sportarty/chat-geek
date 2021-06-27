package ru.gb.chat.server;

import java.sql.SQLException;

/**
 * Created by Artem Kropotov on 24.05.2021
 */
public interface AuthService<T> extends CrudService<T, Long> {
    User findByLoginAndPassword(String login, String password) throws SQLException;
    User findByLoginOrNick(String login, String nick) throws SQLException;
}
