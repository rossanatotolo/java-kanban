package handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest extends HttpTaskServerTest {
    @Test
    public void shouldGetTaskTest() throws IOException, InterruptedException { // get Task
        Task task1 = new Task("Test 1", "Testing task 1", Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 3, 0));
        task1.setId(1);
        Task task2 = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 3, 10));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код не совпадает");

        Task getOfTask = gson.fromJson(response.body(), Task.class);

        assertEquals(getOfTask, task1, "Неверное преобразование задачи или не та задача была получена");
        assertNotEquals(getOfTask, task2, "Не та задача была получена");

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/3")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Статус код не совпадает");
    }

    @Test
    public void shouldGetAllTasksTest() throws IOException, InterruptedException { // getAllTasks
        Task task1 = new Task("Test 1", "Testing task 1", Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 3, 0));
        Task task2 = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 3, 10));

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус код не совпадает");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertTrue(jsonElement.isJsonArray(), "Получен не список");
        assertEquals(jsonArray.size(), 2, "Количество задач неверно");
    }


    @Test
    public void shouldAddTaskTest() throws IOException, InterruptedException { //create Task
        // создаём задачу
        Task task = new Task("Test 1", "Testing task 1", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        //проверяем на пересечение по времени
        Task task2 = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson2 = gson.toJson(task2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode());
    }

    @Test
    public void shouldUpdateTaskTest() throws IOException, InterruptedException { // update Task
        Task task1 = new Task("Test 1", "Testing task 1", Status.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 3, 0));
        String taskJson1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        task1 = new Task("New name", "Testing task 1", Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 3, 0));
        taskManager.updateTask(task1);

        assertEquals(task1.getName(), "New name", "Имя задачи не обновилось");
        assertEquals(task1.getStatus(), Status.NEW, "Статус задачи не изменился");
        assertEquals(201, response.statusCode(), "Статус код не совпадает");
    }

    @Test
    public void shouldDeleteTaskTest() throws IOException, InterruptedException { // delete Task
        Task task1 = new Task("Test 1", "Testing task 1", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 3, 0));

        String taskJson1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        HttpRequest requestDelete = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1")).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseDelete.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Количество подзадач не совпадает");
    }
}