package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeException;
import model.SubTask;
import service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler {

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                subTasksGet(httpExchange);
                break;
            case "POST":
                subTasksPost(httpExchange);
                break;
            case "DELETE":
                subTasksDelete(httpExchange);
                break;
            default:
                sendNotFound(httpExchange, "Endpoint not exist");
        }
    }

    private void subTasksGet(HttpExchange httpExchange) throws IOException {
        String[] splitStrings = httpExchange.getRequestURI().getPath().split("/");
        String response;
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (splitStrings.length == 2) { //get allSubTasks
            try {
                response = gson.toJson(taskManager.getAllSubTask());
                sendText(httpExchange, response);
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else if (postId.isPresent()) { //get subTask(id)
            try {
                SubTask subTask = taskManager.getSubTask(postId.get());
                response = gson.toJson(subTask);
                sendText(httpExchange, response);
            } catch (Exception e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Not found");
        }
    }

    private void subTasksPost(HttpExchange httpExchange) throws IOException {
        String[] splitStrings = httpExchange.getRequestURI().getPath().split("/");
        String requestBody = readText(httpExchange);
        SubTask newSubTask = gson.fromJson(requestBody, SubTask.class);
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (splitStrings.length == 2) { //create subTask
            try {
                taskManager.createSubTask(newSubTask);
                sendSuccess(httpExchange, gson.toJson(taskManager.getAllSubTask()));
            } catch (TimeException e) {
                sendHasInteractions(httpExchange, e.getMessage());
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else if (newSubTask.getId() != 0 && postId.isPresent()) { //update subTask
            try {
                taskManager.updateSubTask(newSubTask);
                sendSuccess(httpExchange, gson.toJson(taskManager.getSubTask(postId.get())));
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

    private void subTasksDelete(HttpExchange httpExchange) throws IOException {
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (postId.isPresent()) { //delete subTask
            try {
                taskManager.deleteSubTask(postId.get());
                sendText(httpExchange, gson.toJson(taskManager.getSubTask(postId.get())));
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