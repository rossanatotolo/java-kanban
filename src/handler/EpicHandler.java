package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeException;
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
            case "GET":
                epicsGet(httpExchange);
                break;
            case "POST":
                epicsPost(httpExchange);
                break;
            case "DELETE":
                epicsDelete(httpExchange);
                break;
            default:
                sendNotFound(httpExchange, "Endpoint not exist");
        }
    }


    private void epicsGet(HttpExchange httpExchange) throws IOException {
        String[] splitStrings = httpExchange.getRequestURI().getPath().split("/");
        String response;
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (splitStrings.length == 2) { //get allEpic
            response = gson.toJson(taskManager.getAllEpic());
            try {
                sendText(httpExchange, response);
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else if (postId.isPresent()) { //get epic(id)
            try {
                Epic epic = taskManager.getEpic(postId.get());
                response = gson.toJson(epic);
                sendText(httpExchange, response);
            } catch (Exception e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Not found");
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
                sendSuccess(httpExchange, gson.toJson(taskManager.getAllEpic()));
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            } catch (Exception e) {
                sendInternalServerError(httpExchange, e.getMessage());
            }
        } else if (newEpic.getId() != 0 && postId.isPresent()) { //update epic
            try {
                taskManager.updateEpic(newEpic);
                sendSuccess(httpExchange, gson.toJson(taskManager.getEpic(postId.get())));
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

    private void epicsDelete(HttpExchange httpExchange) throws IOException {
        Optional<Integer> postId = getIdFromPath(httpExchange);

        if (postId.isPresent()) { //delete epic
            try {
                taskManager.deleteEpic(postId.get());
                sendText(httpExchange, gson.toJson(taskManager.getEpic(postId.get())));
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

