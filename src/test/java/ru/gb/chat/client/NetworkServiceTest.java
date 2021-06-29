package ru.gb.chat.client;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import ru.gb.chat.server.ServerChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class NetworkServiceTest {
    private ServerChat serverChat;
    private static final String IP_ADDRESS = "localhost";
    private static final int PORT = 8189;

    @BeforeClass
    public void startServ(){
        serverChat.start();
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    public void testConnection() {
        Socket socket;
        DataOutputStream out;
        DataInputStream in;
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        }
        catch (IOException e){
            Assertions.fail();
        }
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    public void testReg(String login, String nickname, String password){
        Socket socket;
        DataOutputStream out;
        DataInputStream in;
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            Thread clientListener = new Thread(() -> {
                try {

                    try {
                        out.writeUTF("/register " + login + " " + nickname + " " + password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String msg;
                    while (true) {
                        msg = in.readUTF();
                        if (msg.startsWith("/authok ")) {
                            break;
                        }
                        if (msg.startsWith("/regfail")) {
                            Assertions.fail();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            clientListener.start();
        }
        catch (IOException e){
            Assertions.fail();
        }
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    public void testAuth(String login, String password){
        Socket socket;
        DataOutputStream out;
        DataInputStream in;
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            Thread clientListener = new Thread(() -> {
                try {

                    try {
                        out.writeUTF("/auth " + login + " " + password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String msg;
                    while (true) {
                        msg = in.readUTF();
                        if (msg.startsWith("/authok ")) {
                            break;
                        }
                        if (msg.startsWith("/authfail")) {
                            Assertions.fail();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            clientListener.start();
        }
        catch (IOException e){
            Assertions.fail();
        }
    }
}