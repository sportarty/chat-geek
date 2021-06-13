package ru.gb.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Artem Kropotov on 17.05.2021
 */
public class ServerChat {
    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        new ServerChat().start();
    }

    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(8189)) {
            DBService.connect();
            System.out.println("Сервер запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket, this);
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBService.disconnect();
        }
        System.out.println();
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler client : clients) {
            client.sendMessageWithHistory(msg);
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
            sender.sendMessageWithHistory("Заметка для себя: " + message);
        }

        for (ClientHandler receiver : clients) {
            if (receiver.getUser().getNickname().equals(nick)) {
                receiver.sendMessageWithHistory("от " + sender.getUser().getNickname() + ": " + message);
                sender.sendMessageWithHistory("для " + nick + ": " + message);
                return;
            }
        }
        sender.sendMessageWithHistory("Клиент " + nick + " не найден");
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
}
