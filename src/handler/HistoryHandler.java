package handler;

import com.sun.net.httpserver.HttpExchange;
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
        if (method.equals("GET")) {
            try {
                List<Task> history = taskManager.getHistory();
                sendText(httpExchange, gson.toJson(history));
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Endpoint not exist");
        }
    }
}
