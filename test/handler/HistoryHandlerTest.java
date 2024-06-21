package handler;

import http.HttpTaskServerTest;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryHandlerTest extends HttpTaskServerTest {

    @Test
    void shouldPrioritizedTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2020, 6, 1, 3, 0));
        Task task2 = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2021, 6, 1, 3, 10));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.getTask(1);
        taskManager.getTask(2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код не совпадает");
        List<Task> listTasks = taskManager.getHistory();
        assertEquals(listTasks.size(), 2, "Количество задач не совпадает");
    }
}