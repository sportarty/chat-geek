package ru.gb.chat.client;

import javafx.application.Platform;
import java.sql.*;

public class DBHistoryService implements HistoryService{

    private final static String DB_URL = "jdbc:postgresql://127.0.0.1:5432/gb";
    private final static String USER = "postgres";
    private final static String PASS = "123";

    private static Connection connection;   // Интерфейс подключения
    private static Statement statement;     // Для запросов в БД


    public static void startDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver"); // Загрузка статического контекста драйвера
        System.out.println("Connection to DB Clients");
        connection = DriverManager.getConnection(DB_URL, USER, PASS); // Создание соединения
        statement = connection.createStatement(); // Получение statment из нашего connection
        System.out.println("Connection to DB Clients completed");
    }

    public static void disconnect() {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveHistory(String str) {
        Platform.runLater(() -> {
            // todo добавить фильтр если сообщение уже есть в БД то пропускаем
            String[] iToken = null;
            for (int i = 0; i < str.length(); i++) {
                iToken = str.split("\\n");
            }
            for (int j = 0; j < iToken.length; j++) {
                String[] jToken = iToken[j].split("\\s");
                try {
                    statement.executeUpdate(String.format("INSERT INTO history (nick, msg) VALUES('%s','%s');",jToken[0],jToken[1]));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    public static String loadHistory() {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM history");
            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                String returnStr = "";
                sb.append(resultSet.getString(2) + " " + resultSet.getString(3) + "\n");
            }
            return sb.toString();

        }catch (SQLException e) {
            e.printStackTrace();
            return "Ошибка загрузки истории";
        }
    }
}
