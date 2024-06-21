package handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import http.HttpTaskServerTest;
import model.Epic;
import model.Status;
import model.SubTask;
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

class SubTaskHandlerTest extends HttpTaskServerTest {
    @Test
    public void shouldGetSubTaskTest() throws IOException, InterruptedException { // get subTask
        Epic epic1 = new Epic("Test Epic1", "Test");
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        String subTaskJson = gson.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subTasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        subTask1.setId(2);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subTasks/2")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Статус код не совпадает");

        SubTask subTaskOfGet = gson.fromJson(response.body(), SubTask.class);
        assertEquals(subTaskOfGet, subTask1, "Неверное преобразование задачи или не та задача была получена");

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subTasks/3")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Статус код не совпадает");
    }

    @Test
    public void shouldGetAllSubTasksTest() throws IOException, InterruptedException { // getAllSubTasks
        Epic epic1 = new Epic("Test Epic1", "Test");
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        SubTask subTask2 = new SubTask("Test SubTask2", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 5, 0));

        String subTaskJson1 = gson.toJson(subTask1);
        String subTaskJson2 = gson.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subTasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson2)).build();
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
    public void shouldAddSubTaskTest() throws IOException, InterruptedException { //create subTask
        Epic epic1 = new Epic("Test Epic1", "Test");
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 2, 0));
        String subTaskJson = gson.toJson(subTask1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subTasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> subTasksFromManager = taskManager.getAllSubTask();

        assertNotNull(subTasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test SubTask1", subTasksFromManager.get(0).getName(), "Некорректное имя подзадачи");

        //проверяем на пересечение по времени
        SubTask subTask2 = new SubTask("Test SubTask2", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 2, 2));

        String subTaskJson2 = gson.toJson(subTask2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson2)).build();
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode());
    }

    @Test
    public void shouldUpdateSubTaskTest() throws IOException, InterruptedException { // update subTask
        Epic epic1 = new Epic("Test Epic1", "Test");
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.IN_PROGRESS, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        String subTaskJson1 = gson.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subTasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        subTask1 = new SubTask("New name", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        taskManager.updateSubTask(subTask1);
        assertEquals(subTask1.getName(), "New name", "Имя задачи не обновилось");
        assertEquals(subTask1.getStatus(), Status.NEW, "Статус задачи не изменился");
        assertEquals(201, response.statusCode(), "Статус код не совпадает");
    }

    @Test
    public void shouldDeleteSubTaskTest() throws IOException, InterruptedException { // delete subTask
        Epic epic1 = new Epic("Test Epic1", "Test");
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Test SubTask1", "Test", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 1, 1, 4, 0));
        String subTaskJson1 = gson.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subTasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        HttpRequest requestDelete = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subTasks/2")).DELETE().build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseDelete.statusCode());

        List<SubTask> subTasksFromManager = taskManager.getAllSubTask();
        assertNotNull(subTasksFromManager, "Задачи не возвращаются");
        assertEquals(0, subTasksFromManager.size(), "Количество подзадач не совпадает");
    }
}