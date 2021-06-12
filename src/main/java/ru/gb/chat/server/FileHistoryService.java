package ru.gb.chat.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHistoryService implements HistoryService {
    @Override
    public void save(User user, String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("history.txt", true));
            writer.write(message + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> findHundredByUser(User user) {
        final int MAX_LENGTH = 3; // Для проверки поставил последние 3 сообщения
        List<String> array = new ArrayList<>();
        List<String> history_last_hundred_massages = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("history.txt"));
            String line = reader.readLine();
            while (line != null) {
                array.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (array.size() <= MAX_LENGTH) {
            for (int i = array.size() - 1; i >= 0; i--) {
                history_last_hundred_massages.add(array.get(i));
            }
        } else {
            for (int i = array.size() - 1; i >= array.size() - MAX_LENGTH; i--) {
                history_last_hundred_massages.add(array.get(i));
            }
        }
        return history_last_hundred_massages;
    }
}
