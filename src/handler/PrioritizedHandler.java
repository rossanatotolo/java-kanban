package handler;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.TreeSet;

import static http.HttpMethod.GET;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals(GET)) {
            try {
                TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                generalSend(httpExchange, gson.toJson(prioritizedTasks), 200);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else {
            generalSend(httpExchange, "Endpoint not exist", 404);
        }
    }
}