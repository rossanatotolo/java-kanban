package service;

import http.HttpTaskServer;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static HttpTaskServer getDefaultHttp() {
        return new HttpTaskServer((Managers.getDefault()));
    }
}
