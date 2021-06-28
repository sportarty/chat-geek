package ru.gb.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Artem Kropotov on 17.05.2021
 */
public class ServerChat {
    // База данных по клиентам должна крутиться на сервере, следовательно и запускать БД стоит с сервером
    // А ClientHandler'ы должны только получать доступ к уже имеющимся записям в БД или
    // создавать новую запись при регистрации
    private  String DB_URL = "jdbc:postgresql://127.0.0.1:5432/gb";
    private  String USER = "postgres";
    private  String PASS = "admin";

    private Connection connection;// Интерфейс подключения
    private Statement statement; // Для запросов в БД
    public static Logger logger = Logger.getLogger("");

    public Connection getConnection() {
        return connection;
    }

    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try {
            new ServerChat().start();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws SQLException, ClassNotFoundException, IOException {
        startDB();
        FileHandler fileHandler = new FileHandler("logServerFile.log");
        logger.addHandler(fileHandler);

        ResultSet  resultSet = statement.executeQuery("SELECT * FROM employee");
        while (resultSet.next()) {                                          // перебор элементов
            System.out.println(resultSet.getInt(1) + " " +                  // вывод элементов на консоли сервера
                    resultSet.getString(2) + " " +
                    resultSet.getString(3) + " " +
                    resultSet.getString(4) + " ");
        }

        try(ServerSocket serverSocket = new ServerSocket(8189)) {
            // System.out.println("Сервер запущен");
            logger.log(Level.INFO,"Сервер запущен.");
            while (true) {
                Socket socket = serverSocket.accept();
                // System.out.println("Клиент подключился");
                logger.log(Level.INFO,"Клиент подключился.");
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler client : clients) {
            client.sendMessage(msg);
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public boolean isNickBusy(String nickname) {
        for (ClientHandler c : clients) {
            if (c != null) {
                if (c.getUser().getNickname().equals(nickname)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void privateMsg(ClientHandler sender, String nick, String message) {
        if (sender.getUser().getNickname().equals(nick)) {
            sender.sendMessage("Заметка для себя: " + message);
        }

        for (ClientHandler receiver : clients) {
            if (receiver.getUser().getNickname().equals(nick)) {
                receiver.sendMessage("от " + sender.getUser().getNickname() + ": " + message);
                sender.sendMessage("для " + nick + ": " + message);
                logger.log(Level.INFO, sender.getUser().getNickname() + " послал сообщение для - " + nick);
                return;
            }
        }
        sender.sendMessage("Клиент " + nick + " не найден");
    }

    public void broadcastClientList() {
        StringBuilder stringBuilder = new StringBuilder(9 + 15 * clients.size());
        stringBuilder.append("/clients ");
        // '/clients '
        for (ClientHandler c : clients) {
            stringBuilder.append(c.getUser().getNickname()).append(" ");
        }
        // '/clients nick1 nick2 '
        stringBuilder.setLength(stringBuilder.length() - 1);
        // '/clients nick1 nick2'
        String nickList = stringBuilder.toString();
        for (ClientHandler c : clients) {
            c.sendMessage(nickList);
        }
    }

    public void startDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver"); // Загрузка статического контекста драйвера
        System.out.println("Connection to DB Clients");
        connection = DriverManager.getConnection(DB_URL, USER, PASS); // Создание соединения
        statement = connection.createStatement();// Получение statment из нашего connection
        System.out.println("Connection to DB Clients completed");
    }

    public void disconnect() {
        try { // При закрытии БД нет смысла проброса исключения поэтому обработка здесь
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
