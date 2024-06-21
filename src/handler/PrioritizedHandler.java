package handler;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.TreeSet;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            try {
                TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                sendText(httpExchange, gson.toJson(prioritizedTasks));
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Endpoint not exist");
        }
    }
}