package ru.gb.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.*;

/**
 * Created by Artem Kropotov on 17.05.2021
 */
public class ClientHandler {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ServerChat serverChat;
    //private AuthService<User> authService = ListAuthService.getInstance();
    private User user;
    private AuthService<User> authService = null;
    public static Logger logger = Logger.getLogger("");

    public ClientHandler(Socket socket, ServerChat serverChat) {
        try {
            this.socket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            this.serverChat = serverChat;
            this.authService = DBAuthService.getInstance(serverChat.getConnection());

            FileHandler fileHandler = new FileHandler("logClientFile.log");
            logger.addHandler(fileHandler);

            new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        // /auth login password
                        if (msg.startsWith("/auth ")) {
                            String[] token = msg.split("\\s");
                            User user = authService.findByLoginAndPassword(token[1], token[2]);
                            if (user != null && !serverChat.isNickBusy(user.getNickname())) {
                                sendMessage("/authok " + user.getNickname());
                                this.user = user;
                                serverChat.subscribe(this);
                                logger.log(Level.INFO,"Пользователь: " + user.getLogin() + " - Аутентификация прошла успешно.");
                                break;
                            } else {
                                sendMessage("/authfail");
                                logger.log(Level.WARNING,"Пользователь: " + user.getLogin() + " - Аутентификация не удалась.");
                            }
                            // /register login nickname password
                        } else if (msg.startsWith("/register ")) {
                            String[] token = msg.split("\\s");
                            User user = authService.findByLoginOrNick(token[1], token[2]);
                            if (user == null) {
                                user = authService.save(new User(token[1], token[3], token[2]));
                                sendMessage("/authok " + user.getNickname());
                                this.user = user;
                                serverChat.subscribe(this);
                                break;
                            } else {
                                sendMessage("/regfail");
                                logger.log(Level.WARNING,"Пользователь: " + user.getLogin() + " - Регистрация не удалась.");
                            }
                        }
                    }
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {
                            if (msg.equals("/end")) {
                                sendMessage("/end");
                                break;
                            }
                            // /w nick fg sdg sdfg sd
                            if (msg.startsWith("/w")) {
                                String[] token = msg.split("\\s", 3);
                                serverChat.privateMsg(this, token[1], token[2]);

                            }
                            if (msg.equals("/del")) {
                                logger.log(Level.INFO,"Пользователь: " + user.getLogin() + " - Удален");
                                authService.remove(user);
                                sendMessage("/end");
                                break;
                            }
                        } else {
                            serverChat.broadcastMsg(user.getNickname() + ": " + msg);
                            logger.log(Level.INFO, user.getNickname() + " послал сообщение для всех");
                        }
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                } finally {
                    // System.out.println("Клиент отключился");
                    logger.log(Level.INFO,"Пользователь: " + user.getLogin() + " - Отключился");
                    disconnect();
                }
            }).start();
        } catch (IOException | SQLException e) {
            disconnect();
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
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
