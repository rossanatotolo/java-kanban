package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                tasksGet(httpExchange);
                break;
            case "POST":
                tasksPost(httpExchange);
                break;
            case "DELETE":
                tasksDelete(httpExchange);
                break;
            default:
                sendNotFound(httpExchange, "Endpoint not exist");
        }
    }

    private void tasksGet(HttpExchange httpExchange) throws IOException {
        String[] splitStrings = httpExchange.getRequestURI().getPath().split("/");
        String response;
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (splitStrings.length == 2) { //get allTasks
            response = gson.toJson(taskManager.getAllTask());
            try {
                sendText(httpExchange, response);
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else if (postId.isPresent()) { //get task(id)
            try {
                Task task = taskManager.getTask(postId.get());
                response = gson.toJson(task);
                sendText(httpExchange, response);
            } catch (Exception e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Not found");
        }
    }

    private void tasksPost(HttpExchange httpExchange) throws IOException {
        String[] splitStrings = httpExchange.getRequestURI().getPath().split("/");
        String requestBody = readText(httpExchange);
        Task newTask = gson.fromJson(requestBody, Task.class);
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (splitStrings.length == 2) { //create Task
            try {
                taskManager.createTask(newTask);
                sendSuccess(httpExchange, gson.toJson(taskManager.getAllTask()));
            } catch (TimeException e) {
                sendHasInteractions(httpExchange, e.getMessage());
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else if (newTask.getId() != 0 && postId.isPresent()) { //update Task
            try {
                taskManager.updateTask(newTask);
                sendSuccess(httpExchange, gson.toJson(taskManager.getTask(postId.get())));
            } catch (TimeException e) {
                sendHasInteractions(httpExchange, e.getMessage());
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Not found");
        }
    }

    private void tasksDelete(HttpExchange httpExchange) throws IOException {
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (postId.isPresent()) { //delete Task
            try {
                taskManager.deleteTask(postId.get());
                sendText(httpExchange, gson.toJson(taskManager.getTask(postId.get())));
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Not found");
        }
    }
}


