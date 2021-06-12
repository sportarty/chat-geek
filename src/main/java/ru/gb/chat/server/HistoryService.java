package ru.gb.chat.server;

import java.io.IOException;
import java.util.List;

public interface HistoryService {
   void save(User user, String message) throws IOException;
    List<String> findHundredByUser(User user);
}
