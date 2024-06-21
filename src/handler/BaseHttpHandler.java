package handler;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    protected Optional<Integer> getIdFromPath(HttpExchange httpExchange) {
        String[] parts = httpExchange.getRequestURI().getPath().split("/");

        if (parts.length > 2) {
            try {
                int postId = Integer.parseInt(parts[2]);
                return Optional.of(postId);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("id не является числом");
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(200, resp.length);
            h.getResponseBody().write(resp);
        } catch (IOException exp) {
            exp.printStackTrace();
        } finally {
            h.close();
        }
    }

    //считывание тела запроса
    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendSuccess(HttpExchange h, String text) throws IOException {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(201, resp.length);
            h.getResponseBody().write(resp);
        } catch (IOException exp) {
            exp.printStackTrace();
        } finally {
            h.close();
        }
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(404, resp.length);
            h.getResponseBody().write(resp);
        } catch (IOException exp) {
            exp.printStackTrace();
        } finally {
            h.close();
        }
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(406, resp.length);
            h.getResponseBody().write(resp);
        } catch (IOException exp) {
            exp.printStackTrace();
        } finally {
            h.close();
        }
    }

    protected void sendInternalServerError(HttpExchange h, String text) throws IOException {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(500, resp.length);
            h.getResponseBody().write(resp);
        } catch (IOException exp) {
            exp.printStackTrace();
        } finally {
            h.close();
        }
    }
}