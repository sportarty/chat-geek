package ru.gb.chat.server;

import java.util.List;

/**
 * Created by Artem Kropotov on 10.06.2021
 */
public interface HistoryService {
    boolean save(User user, String message);
    /**
     * @return последние 100 сообщений
     */
    List<String> findAllByUser(User user);
}
