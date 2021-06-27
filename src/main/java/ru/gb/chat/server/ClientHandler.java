package ru.gb.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

/**
 * Created by Artem Kropotov on 17.05.2021
 */
public class ClientHandler {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ServerChat serverChat;
    private final AuthService<User> authService = DBAuthService.getInstance();
    private final HistoryService historyService = DBHistoryService.getInstance();
    private User user;

    public ClientHandler(Socket socket, ServerChat serverChat) {
        try {
            this.socket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            this.serverChat = serverChat;

            new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        // /auth login password
                        if (msg.startsWith("/auth ")) {
                            String[] token = msg.split("\\s");
                            Optional<User> userOptional = authService.findByLoginAndPassword(token[1], token[2]);
                            if (userOptional.isPresent()) {
                                User user = userOptional.get();
                                if (!serverChat.isNickBusy(user.getNickname())) {
                                    this.user = user;
                                    successAuthorization();
                                    break;
                                }
                            } else {
                                sendServiceMessage("/authfail");
                            }
                            // /register login nickname password
                        } else if (msg.startsWith("/register ")) {
                            String[] token = msg.split("\\s");
                            Optional<User> userOptional = authService.findByLoginOrNick(token[1], token[2]);
                            if (!userOptional.isPresent()) {
                                this.user = authService.save(new User(token[1], token[3], token[2]));
                                successAuthorization();
                                break;
                            } else {
                                sendServiceMessage("/regfail");
                            }
                        }
                    }

                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {
                            if (msg.equals("/end")) {
                                sendServiceMessage("/end");
                                break;
                            }
                            // /w nick fg sdg sdfg sd
                            if (msg.startsWith("/w")) {
                                String[] token = msg.split("\\s", 3);
                                serverChat.privateMsg(this, token[1], token[2]);

                            }
                            if (msg.equals("/del")) {
                                authService.remove(user);
                                sendServiceMessage("/end");
                                break;
                            }
                        } else {
                            serverChat.broadcastMsg(user.getNickname() + ": " + msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Клиент отключился");
                    disconnect();
                }
            }).start();
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();
        }
    }

    private void successAuthorization() {
        sendServiceMessage("/authok " + user.getNickname());
        List<String> messages = historyService.findAllByUser(user);
        for(String msg : messages) {
            sendServiceMessage(msg);
        }
        serverChat.subscribe(this);
    }

    public void sendMessageWithHistory(String message) {
        try {
            historyService.save(user, message);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendServiceMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        serverChat.unsubscribe(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }
}
