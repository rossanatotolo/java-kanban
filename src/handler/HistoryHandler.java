package handler;

import com.sun.net.httpserver.HttpExchange;
import http.HttpMethod;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals(HttpMethod.GET)) {
            try {
                List<Task> history = taskManager.getHistory();
                generalSend(httpExchange, gson.toJson(history), 200);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else {
            generalSend(httpExchange, "Endpoint not exist", 404);
        }
    }
}
