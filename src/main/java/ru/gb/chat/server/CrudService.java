package ru.gb.chat.server;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Artem Kropotov on 24.05.2021
 */
public interface CrudService<T, ID> {
    T save(T object) throws SQLException;

    T remove(T object) throws SQLException;

    T changeNick(T object) throws SQLException;

    T removeById(ID id);

    T findById(ID id);

    List<T> findAll();

}
