package ru.gb.chat.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBHistoryService implements HistoryService {
    private static DBHistoryService INSTANCE;

    public static DBHistoryService getInstance() throws SQLException {
        if (INSTANCE == null) {
            synchronized (DBHistoryService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBHistoryService();
                }
            }
        }
        return INSTANCE;
    }

    private final PreparedStatement psSaveMessage;
    private final PreparedStatement psFindLastMessage;

    public DBHistoryService() throws SQLException {
        psFindLastMessage = DBServise.createPreparedStatement("SELECT * FROM(" + "SELECT * FROM history WHERE nickname = ? ORDER BY id DESC LIMIT 100" + ") AS latest " + "ORDER BY id");
        psSaveMessage = DBServise.createPreparedStatement("INSERT INTO history(nickname, message) VALUES (?,?);");

    }


    @Override
    public void save(User user, String message) {
        try {
            psSaveMessage.setString(1, user.getNickname());
            psSaveMessage.setString(2, message);
            psSaveMessage.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> findHundredByUser(User user) {
        try {
            psFindLastMessage.setString(1, user.getNickname());
            ResultSet resultSet = psFindLastMessage.executeQuery();
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
