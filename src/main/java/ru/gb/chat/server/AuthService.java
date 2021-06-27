package ru.gb.chat.server;

import java.util.Optional;

/**
 * Created by Artem Kropotov on 24.05.2021
 */
public interface AuthService<T> extends CrudService<T, Long> {
    Optional<User> findByLoginAndPassword(String login, String password);
    Optional<User> findByLoginOrNick(String login, String nick);
}
