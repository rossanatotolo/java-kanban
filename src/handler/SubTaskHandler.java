package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeException;
import http.HttpMethod;
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
            case HttpMethod.GET:
                subTasksGet(httpExchange);
                break;
            case HttpMethod.POST:
                subTasksPost(httpExchange);
                break;
            case HttpMethod.DELETE:
                subTasksDelete(httpExchange);
                break;
            default:
                generalSend(httpExchange, "Endpoint not exist", 404);
        }
    }

    private void subTasksGet(HttpExchange httpExchange) throws IOException {
        String[] splitStrings = httpExchange.getRequestURI().getPath().split("/");
        String response;
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (splitStrings.length == 2) { //get allSubTasks
            try {
                response = gson.toJson(taskManager.getAllSubTask());
                generalSend(httpExchange, response, 200);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else if (postId.isPresent()) { //get subTask(id)
            try {
                SubTask subTask = taskManager.getSubTask(postId.get());
                response = gson.toJson(subTask);
                generalSend(httpExchange, response, 200);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 404);
            }
        } else {
            generalSend(httpExchange, "Not found", 404);
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
                generalSend(httpExchange, gson.toJson(taskManager.getAllSubTask()), 201);
            } catch (TimeException e) {
                generalSend(httpExchange, e.getMessage(), 406);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else if (newSubTask.getId() != 0 && postId.isPresent()) { //update subTask
            try {
                taskManager.updateSubTask(newSubTask);
                generalSend(httpExchange, gson.toJson(taskManager.getSubTask(postId.get())), 201);
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

    private void subTasksDelete(HttpExchange httpExchange) throws IOException {
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (postId.isPresent()) { //delete subTask
            try {
                taskManager.deleteSubTask(postId.get());
                generalSend(httpExchange, gson.toJson(taskManager.getSubTask(postId.get())), 200);
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