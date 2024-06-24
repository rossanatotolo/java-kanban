package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeException;
import http.HttpMethod;
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
            case HttpMethod.GET:
                tasksGet(httpExchange);
                break;
            case HttpMethod.POST:
                tasksPost(httpExchange);
                break;
            case HttpMethod.DELETE:
                tasksDelete(httpExchange);
                break;
            default:
                generalSend(httpExchange, "Endpoint not exist", 404);
        }
    }

    private void tasksGet(HttpExchange httpExchange) throws IOException {
        String[] splitStrings = httpExchange.getRequestURI().getPath().split("/");
        String response;
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (splitStrings.length == 2) { //get allTasks
            response = gson.toJson(taskManager.getAllTask());
            try {
                generalSend(httpExchange, response, 200);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else if (postId.isPresent()) { //get task(id)
            try {
                Task task = taskManager.getTask(postId.get());
                response = gson.toJson(task);
                generalSend(httpExchange, response, 200);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 404);
            }
        } else {
            generalSend(httpExchange, "Not found", 404);
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
                generalSend(httpExchange, gson.toJson(taskManager.getAllTask()), 201);
            } catch (TimeException e) {
                generalSend(httpExchange, e.getMessage(), 406);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else if (newTask.getId() != 0 && postId.isPresent()) { //update Task
            try {
                taskManager.updateTask(newTask);
                generalSend(httpExchange, gson.toJson(taskManager.getTask(postId.get())), 201);
            } catch (TimeException e) {
                generalSend(httpExchange, e.getMessage(), 406);
            } catch (NotFoundException e) {
                generalSend(httpExchange, e.getMessage(), 404);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else {
            generalSend(httpExchange, "Not found", 404);
        }
    }

    private void tasksDelete(HttpExchange httpExchange) throws IOException {
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (postId.isPresent()) { //delete Task
            try {
                taskManager.deleteTask(postId.get());
                generalSend(httpExchange, gson.toJson(taskManager.getTask(postId.get())), 200);
            } catch (NotFoundException e) {
                generalSend(httpExchange, e.getMessage(), 404);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else {
            generalSend(httpExchange, "Not found", 404);
        }
    }
}


