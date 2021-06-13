package ru.gb.chat.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBHistoryService implements HistoryService {
    private static DBHistoryService INSTANCE;

    public static DBHistoryService getInstance() {
        if (INSTANCE == null) {
            synchronized (DBHistoryService.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = new DBHistoryService();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }
        return INSTANCE;
    }

    private final PreparedStatement psSaveMessage;
    private final PreparedStatement psFindLastMessages;

    public DBHistoryService() throws SQLException {
        psFindLastMessages = DBService.createPreparedStatement("SELECT * FROM(" + "SELECT * FROM history WHERE nickname = ? ORDER BY id DESC LIMIT 100" + ") AS latest " + "ORDER BY id");
        psSaveMessage = DBService.createPreparedStatement("INSERT INTO history(nickname, message) VALUES (?,?);");
    }

    @Override
    public void save(User user, String message) {
        try {
            psSaveMessage.setString(1, user.getNickname());
            psSaveMessage.setString(2, message);
            psSaveMessage.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public List<String> findForUser(User user) {
        try {
            psFindLastMessages.setString(1, user.getNickname());
            ResultSet resultSet = psFindLastMessages.executeQuery();
            List<String> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(resultSet.getString("message"));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
