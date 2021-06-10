package ru.gb.chat.client;

import javafx.application.Platform;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileHistoryService implements HistoryService {
    public static final String HS_FILE = "HistoryChat.hisdb";
    public static final int HS_SIZE = 100;

    public static void saveHistory(String str) {
        Platform.runLater(() -> {
            File file = new File(HS_FILE);
            try (OutputStream os = new FileOutputStream(file)) {
                os.write(str.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static String loadHistory() {
        File file = new File(HS_FILE);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            StringBuilder sb = new StringBuilder();
            String str;
            int i = 0;
            while ((str = br.readLine()) != null) {
                if(i < HS_SIZE) {
                    sb.append(str);
                    sb.append("\n");
                    i++;
                }
                else break;
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
