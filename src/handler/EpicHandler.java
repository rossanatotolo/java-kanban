package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeException;
import http.HttpMethod;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case HttpMethod.GET:
                epicsGet(httpExchange);
                break;
            case HttpMethod.POST:
                epicsPost(httpExchange);
                break;
            case HttpMethod.DELETE:
                epicsDelete(httpExchange);
                break;
            default:
                generalSend(httpExchange, "Endpoint not exist", 404);
        }
    }

    private void epicsGet(HttpExchange httpExchange) throws IOException {
        String[] splitStrings = httpExchange.getRequestURI().getPath().split("/");
        String response;
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (splitStrings.length == 2) { //get allEpic
            response = gson.toJson(taskManager.getAllEpic());
            try {
                generalSend(httpExchange, response, 200);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else if (postId.isPresent()) { //get epic(id)
            try {
                Epic epic = taskManager.getEpic(postId.get());
                response = gson.toJson(epic);
                generalSend(httpExchange, response, 200);
            } catch (NotFoundException e) {
                generalSend(httpExchange, e.getMessage(), 404);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else {
            generalSend(httpExchange, "Not found", 404);
        }
    }

    private void epicsPost(HttpExchange httpExchange) throws IOException {
        String[] splitStrings = httpExchange.getRequestURI().getPath().split("/");
        String requestBody = readText(httpExchange);
        Epic newEpic = gson.fromJson(requestBody, Epic.class);
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (splitStrings.length == 2) { //create epic
            try {
                taskManager.createEpic(newEpic);
                generalSend(httpExchange, gson.toJson(taskManager.getAllEpic()), 201);
            } catch (NotFoundException e) {
                generalSend(httpExchange, e.getMessage(), 404);
            } catch (Exception e) {
                generalSend(httpExchange, e.getMessage(), 500);
            }
        } else if (newEpic.getId() != 0 && postId.isPresent()) { //update epic
            try {
                taskManager.updateEpic(newEpic);
                generalSend(httpExchange, gson.toJson(taskManager.getEpic(postId.get())), 201);
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

    private void epicsDelete(HttpExchange httpExchange) throws IOException {
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (postId.isPresent()) { //delete epic
            try {
                taskManager.deleteEpic(postId.get());
                generalSend(httpExchange, gson.toJson(taskManager.getEpic(postId.get())), 200);
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

