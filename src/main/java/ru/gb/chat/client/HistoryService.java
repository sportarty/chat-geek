package ru.gb.chat.client;

interface HistoryService {
    static void saveHistory(String str) {}

    static String loadHistory() {
        return " ";
    }
}
