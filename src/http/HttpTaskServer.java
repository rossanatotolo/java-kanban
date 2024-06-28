package http;

import com.sun.net.httpserver.HttpServer;
import handler.EpicHandler;
import handler.HistoryHandler;
import handler.SubTaskHandler;
import handler.TaskHandler;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    static final int PORT = 8080;
    private HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpServer1 = Managers.getDefaultHttp();
        httpServer1.start();
        //httpServer1.stop();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subTasks", new SubTaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new TaskHandler(taskManager));

        httpServer.start();
        System.out.println("Server start on the PORT: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server stop on the PORT " + PORT);
    }
}
