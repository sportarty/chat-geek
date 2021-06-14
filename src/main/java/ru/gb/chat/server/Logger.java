package ru.gb.chat.server;

import java.sql.SQLException;
import java.util.List;

public interface Logger <T> {
    boolean add(T t);
    List<T> getNumLast(int num);
    void close();
}
