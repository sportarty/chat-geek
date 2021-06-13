package ru.gb.chat.server;

import java.util.List;

public interface HistoryService {
    void save(User user, String message);

    List <String> findForUser(User user);
}
